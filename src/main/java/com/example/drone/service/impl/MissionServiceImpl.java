package com.example.drone.service.impl;

import com.example.drone.domain.dto.*;
import com.example.drone.domain.entity.FlightMission;
import com.example.drone.domain.enums.MissionStatus;
import com.example.drone.domain.enums.MissionType;
import com.example.drone.domain.entity.Drone;
import com.example.drone.exception.DroneBusinessException;
import com.example.drone.exception.DroneNotFoundException;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.FlightMissionRepository;
import com.example.drone.service.MissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MissionServiceImpl implements MissionService {

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final FlightMissionRepository missionRepo;
    private final DroneRepository droneRepo;

    public MissionServiceImpl(FlightMissionRepository missionRepo, DroneRepository droneRepo) {
        this.missionRepo = missionRepo;
        this.droneRepo = droneRepo;
    }

    @Override @Transactional
    public MissionDTO createMission(CreateMissionRequest request) {
        if (droneRepo.selectById(request.getDroneId()) == null)
            throw new DroneNotFoundException("无人机不存在，ID: " + request.getDroneId());
        FlightMission mission = FlightMission.builder()
                .missionCode("M" + System.currentTimeMillis()).droneId(request.getDroneId())
                .missionType(request.getMissionType()).status(MissionStatus.PENDING)
                .pilotName(request.getPilotName())
                .plannedStartTime(request.getPlannedStartTime() != null
                        ? LocalDateTime.parse(request.getPlannedStartTime(), DT_FORMAT) : null)
                .plannedEndTime(request.getPlannedEndTime() != null
                        ? LocalDateTime.parse(request.getPlannedEndTime(), DT_FORMAT) : null)
                .flightArea(request.getFlightArea()).remarks(request.getRemarks()).build();
        int rows = missionRepo.insert(mission);
        if (rows != 1) throw new DroneBusinessException(500, "创建飞行任务失败");
        return toDTO(missionRepo.selectById(mission.getId()));
    }

    @Override @Transactional
    public MissionDTO getMissionById(Long id) {
        FlightMission m = missionRepo.selectById(id);
        if (m == null) throw new DroneNotFoundException("飞行任务不存在，ID: " + id);
        refreshStatus(m);
        return toDTO(m);
    }

    @Override @Transactional
    public Page<MissionDTO> listMissions(MissionQueryRequest request) {
        // DB层不按status筛选——因为status可能被refreshStatus改变
        // 只按missionType/droneId/pilotName查DB
        FlightMission condition = new FlightMission();
        boolean hasStatus = request.getStatus() != null && !request.getStatus().isEmpty();
        MissionStatus queryStatus = hasStatus ? MissionStatus.valueOf(request.getStatus()) : null;
        if (request.getMissionType() != null && !request.getMissionType().isEmpty())
            condition.setMissionType(MissionType.valueOf(request.getMissionType()));
        condition.setDroneId(request.getDroneId());
        condition.setPilotName(request.getPilotName());

        List<FlightMission> dbList = missionRepo.selectByCondition(condition);
        // 自动刷新状态
        for (FlightMission m : dbList) { refreshStatus(m); }

        // 时间筛选：任务与[开始时间, 结束时间]有重叠即显示
        boolean hasStartFrom = request.getStartFrom() != null && !request.getStartFrom().isEmpty();
        boolean hasStartTo   = request.getStartTo()   != null && !request.getStartTo().isEmpty();

        List<MissionDTO> dtos = new ArrayList<>();
        for (FlightMission m : dbList) {
            // 时间重叠筛选
            if (hasStartFrom && hasStartTo) {
                LocalDateTime qStart = LocalDateTime.parse(request.getStartFrom(), DT_FORMAT);
                LocalDateTime qEnd   = LocalDateTime.parse(request.getStartTo(), DT_FORMAT);
                // 有开始时间 → 必须满足 task.start <= qEnd
                if (m.getPlannedStartTime() != null && m.getPlannedStartTime().isAfter(qEnd)) continue;
                // 有结束时间 → 必须满足 task.end >= qStart
                if (m.getPlannedEndTime() != null && m.getPlannedEndTime().isBefore(qStart)) continue;
                // 两个都 null → 不显示
                if (m.getPlannedStartTime() == null && m.getPlannedEndTime() == null) continue;
            } else if (hasStartFrom) {
                // 只有开始时间 → task.end >= qStart
                LocalDateTime qStart = LocalDateTime.parse(request.getStartFrom(), DT_FORMAT);
                if (m.getPlannedEndTime() == null || m.getPlannedEndTime().isBefore(qStart)) continue;
            } else if (hasStartTo) {
                // 只有结束时间 → task.start <= qEnd
                LocalDateTime qEnd = LocalDateTime.parse(request.getStartTo(), DT_FORMAT);
                if (m.getPlannedStartTime() == null || m.getPlannedStartTime().isAfter(qEnd)) continue;
            }
            // 状态筛选（在refreshStatus之后）
            if (hasStatus) {
                if (m.getStatus() != queryStatus) continue;
            } else {
                // 默认不显示已取消
                if (m.getStatus() == MissionStatus.CANCELLED) continue;
            }
            dtos.add(toDTO(m));
        }
        return Page.<MissionDTO>builder().total((long) dtos.size())
                .pageNum(request.getPageNum()).pageSize(request.getPageSize())
                .pages(0).list(dtos).build();
    }

    @Override @Transactional
    public MissionDTO updateMission(Long id, MissionDTO request) {
        FlightMission existing = missionRepo.selectById(id);
        if (existing == null) throw new DroneNotFoundException("飞行任务不存在，ID: " + id);
        FlightMission.FlightMissionBuilder builder = FlightMission.builder().id(id);
        if (request.getPlannedStartTime() != null && !request.getPlannedStartTime().isEmpty()) {
            LocalDateTime newStart = LocalDateTime.parse(request.getPlannedStartTime(), DT_FORMAT);
            if (newStart.isBefore(LocalDateTime.now()))
                throw new DroneBusinessException(400, "计划开始时间不能早于当前系统时间");
            builder.plannedStartTime(newStart);
        } else {
            builder.plannedStartTime(existing.getPlannedStartTime());
        }
        if (request.getPlannedEndTime() != null && !request.getPlannedEndTime().isEmpty()) {
            builder.plannedEndTime(LocalDateTime.parse(request.getPlannedEndTime(), DT_FORMAT));
        } else {
            builder.plannedEndTime(existing.getPlannedEndTime());
        }
        if (request.getMissionType() != null && !request.getMissionType().isEmpty())
            builder.missionType(MissionType.valueOf(request.getMissionType()));
        else
            builder.missionType(existing.getMissionType());
        builder.status(existing.getStatus())
               .pilotName(request.getPilotName() != null ? request.getPilotName() : existing.getPilotName())
               .flightArea(request.getFlightArea() != null ? request.getFlightArea() : existing.getFlightArea())
               .remarks(request.getRemarks() != null ? request.getRemarks() : existing.getRemarks());
        missionRepo.updateById(builder.build());
        return toDTO(missionRepo.selectById(id));
    }

    @Override @Transactional
    public void cancelMission(Long id) {
        FlightMission m = missionRepo.selectById(id);
        if (m == null) throw new DroneNotFoundException("飞行任务不存在，ID: " + id);
        missionRepo.updateById(FlightMission.builder().id(id).status(MissionStatus.CANCELLED).build());
    }

    @Override @Transactional
    public void permanentlyDeleteMission(Long id) {
        if (missionRepo.selectById(id) == null)
            throw new DroneNotFoundException("飞行任务不存在，ID: " + id);
        missionRepo.deleteById(id);
    }

    @Override @Transactional
    public void deleteMission(Long id) {
        cancelMission(id);
    }

    @Override @Transactional
    public MissionDTO restoreMission(Long id) {
        FlightMission m = missionRepo.selectById(id);
        if (m == null) throw new DroneNotFoundException("飞行任务不存在，ID: " + id);
        if (m.getStatus() != MissionStatus.CANCELLED)
            throw new DroneBusinessException(400, "只能恢复已取消的任务");
        LocalDateTime now = LocalDateTime.now();
        if (m.getPlannedEndTime() != null && now.isAfter(m.getPlannedEndTime()))
            throw new DroneBusinessException(400, "恢复失败：任务结束时间已过");
        missionRepo.updateById(FlightMission.builder().id(id).status(MissionStatus.PENDING).build());
        return toDTO(missionRepo.selectById(id));
    }

    @Override @Transactional
    public MissionDTO completeMission(Long id) {
        FlightMission existing = missionRepo.selectById(id);
        if (existing == null) throw new DroneNotFoundException("飞行任务不存在，ID: " + id);
        FlightMission update = FlightMission.builder().id(id)
                .status(MissionStatus.COMPLETED).actualEndTime(LocalDateTime.now()).build();
        missionRepo.updateById(update);
        return toDTO(missionRepo.selectById(id));
    }

    @Override @Transactional(readOnly = true)
    public List<MissionDTO> getMissionsByDroneId(Long droneId) {
        FlightMission cond = new FlightMission();
        cond.setDroneId(droneId);
        return missionRepo.selectByCondition(cond).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    private void refreshStatus(FlightMission m) {
        if (m == null || m.getStatus() == null) return;
        LocalDateTime now = LocalDateTime.now();
        if (m.getStatus() == MissionStatus.PENDING && m.getPlannedStartTime() != null
                && !now.isBefore(m.getPlannedStartTime())) {
            m.setStatus(MissionStatus.IN_PROGRESS);
            m.setActualStartTime(now);
        }
        if (m.getStatus() == MissionStatus.IN_PROGRESS && m.getPlannedEndTime() != null
                && !now.isBefore(m.getPlannedEndTime())) {
            m.setStatus(MissionStatus.COMPLETED);
            m.setActualEndTime(now);
        }
    }

    private MissionDTO toDTO(FlightMission m) {
        if (m == null) return null;
        Drone drone = droneRepo.selectById(m.getDroneId());
        return MissionDTO.builder().id(m.getId()).missionCode(m.getMissionCode())
                .droneId(m.getDroneId())
                .droneSerialNumber(drone != null ? drone.getSerialNumber() : null)
                .missionType(m.getMissionType() != null ? m.getMissionType().getDescription() : null)
                .status(m.getStatus() != null ? m.getStatus().getDescription() : null)
                .pilotName(m.getPilotName())
                .plannedStartTime(m.getPlannedStartTime() != null ? m.getPlannedStartTime().format(DT_FORMAT) : null)
                .plannedEndTime(m.getPlannedEndTime() != null ? m.getPlannedEndTime().format(DT_FORMAT) : null)
                .actualStartTime(m.getActualStartTime() != null ? m.getActualStartTime().format(DT_FORMAT) : null)
                .actualEndTime(m.getActualEndTime() != null ? m.getActualEndTime().format(DT_FORMAT) : null)
                .flightArea(m.getFlightArea()).remarks(m.getRemarks())
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().format(DT_FORMAT) : null)
                .updatedAt(m.getUpdatedAt() != null ? m.getUpdatedAt().format(DT_FORMAT) : null)
                .build();
    }
}
