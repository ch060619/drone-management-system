package com.example.drone.service.impl;

import com.example.drone.domain.dto.*;
import com.example.drone.domain.entity.Drone;
import com.example.drone.domain.entity.MaintenanceRecord;
import com.example.drone.domain.enums.MaintenanceType;
import com.example.drone.exception.DroneBusinessException;
import com.example.drone.exception.DroneNotFoundException;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.MaintenanceRepository;
import com.example.drone.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final MaintenanceRepository mtnRepo;
    private final DroneRepository droneRepo;

    public MaintenanceServiceImpl(MaintenanceRepository mtnRepo, DroneRepository droneRepo) {
        this.mtnRepo = mtnRepo;
        this.droneRepo = droneRepo;
    }

    @Override @Transactional
    public MaintenanceDTO createMaintenance(CreateMaintenanceRequest request) {
        if (droneRepo.selectById(request.getDroneId()) == null)
            throw new DroneNotFoundException("无人机不存在，ID: " + request.getDroneId());
        if (request.getMaintenanceType() == null || request.getMaintenanceType().isEmpty())
            throw new DroneBusinessException(400, "维护类型不能为空");
        MaintenanceRecord record = MaintenanceRecord.builder()
                .droneId(request.getDroneId())
                .maintenanceType(MaintenanceType.valueOf(request.getMaintenanceType()))
                .status("PENDING").scheduledDate(LocalDate.parse(request.getScheduledDate(), DATE_FORMAT))
                .technician(request.getTechnician()).description(request.getDescription()).build();
        int rows = mtnRepo.insert(record);
        if (rows != 1) throw new DroneBusinessException(500, "创建维护记录失败");
        return toDTO(mtnRepo.selectById(record.getId()));
    }

    @Override @Transactional(readOnly = true)
    public MaintenanceDTO getMaintenanceById(Long id) {
        MaintenanceRecord r = mtnRepo.selectById(id);
        if (r == null) throw new DroneNotFoundException("维护记录不存在，ID: " + id);
        return toDTO(r);
    }

    @Override @Transactional(readOnly = true)
    public Page<MaintenanceDTO> listMaintenance(MaintenanceQueryRequest request) {
        MaintenanceRecord cond = new MaintenanceRecord();
        cond.setDroneId(request.getDroneId());
        cond.setStatus(request.getStatus());
        int total = mtnRepo.countByCondition(cond);
        List<MaintenanceRecord> list = mtnRepo.selectByCondition(cond);
        List<MaintenanceDTO> dtos = list.stream().map(this::toDTO).collect(Collectors.toList());
        int pages = total == 0 ? 0 : (int) Math.ceil((double) total / request.getPageSize());
        return Page.<MaintenanceDTO>builder().total((long) total)
                .pageNum(request.getPageNum()).pageSize(request.getPageSize())
                .pages(pages).list(dtos).build();
    }

    @Override @Transactional
    public MaintenanceDTO updateMaintenance(Long id, MaintenanceDTO request) {
        MaintenanceRecord r = MaintenanceRecord.builder().id(id)
                .status(request.getStatus())
                .completedDate(request.getCompletedDate() != null
                        ? LocalDate.parse(request.getCompletedDate(), DATE_FORMAT) : null)
                .technician(request.getTechnician()).partsReplaced(request.getPartsReplaced())
                .cost(request.getCost()).description(request.getDescription())
                .nextMaintenanceDate(request.getNextMaintenanceDate() != null
                        ? LocalDate.parse(request.getNextMaintenanceDate(), DATE_FORMAT) : null).build();
        mtnRepo.updateById(r);
        return toDTO(mtnRepo.selectById(id));
    }

    @Override @Transactional
    public void deleteMaintenance(Long id) { mtnRepo.deleteById(id); }

    @Override @Transactional
    public MaintenanceDTO completeMaintenance(Long id, String technician, String partsReplaced,
                                               Integer cost, String description) {
        MaintenanceRecord r = MaintenanceRecord.builder().id(id).status("COMPLETED")
                .completedDate(LocalDate.now()).technician(technician)
                .partsReplaced(partsReplaced).cost(cost).description(description).build();
        mtnRepo.updateById(r);
        return toDTO(mtnRepo.selectById(id));
    }

    @Override @Transactional(readOnly = true)
    public List<MaintenanceDTO> getPendingMaintenance() {
        return mtnRepo.selectPending().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private MaintenanceDTO toDTO(MaintenanceRecord r) {
        if (r == null) return null;
        Drone drone = droneRepo.selectById(r.getDroneId());
        return MaintenanceDTO.builder().id(r.getId()).droneId(r.getDroneId())
                .droneSerialNumber(drone != null ? drone.getSerialNumber() : null)
                .maintenanceType(r.getMaintenanceType() != null ? r.getMaintenanceType().getDescription() : null)
                .status(r.getStatus())
                .scheduledDate(r.getScheduledDate() != null ? r.getScheduledDate().format(DATE_FORMAT) : null)
                .completedDate(r.getCompletedDate() != null ? r.getCompletedDate().format(DATE_FORMAT) : null)
                .technician(r.getTechnician()).partsReplaced(r.getPartsReplaced())
                .cost(r.getCost()).description(r.getDescription())
                .nextMaintenanceDate(r.getNextMaintenanceDate() != null
                        ? r.getNextMaintenanceDate().format(DATE_FORMAT) : null)
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().format(DT_FORMAT) : null)
                .updatedAt(r.getUpdatedAt() != null ? r.getUpdatedAt().format(DT_FORMAT) : null).build();
    }
}
