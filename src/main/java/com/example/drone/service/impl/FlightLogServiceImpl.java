package com.example.drone.service.impl;

import com.example.drone.domain.dto.*;
import com.example.drone.domain.entity.Drone;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.FlightLogRepository;
import com.example.drone.service.FlightLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.drone.domain.entity.FlightLog;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlightLogServiceImpl implements FlightLogService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final FlightLogRepository logRepo;
    private final DroneRepository droneRepo;

    public FlightLogServiceImpl(FlightLogRepository logRepo, DroneRepository droneRepo) {
        this.logRepo = logRepo;
        this.droneRepo = droneRepo;
    }

    @Override @Transactional
    public FlightLogDTO createFlightLog(CreateFlightLogRequest request) {
        FlightLog log = FlightLog.builder()
                .droneId(request.getDroneId()).missionId(request.getMissionId())
                .flightDuration(request.getFlightDuration())
                .flightDistance(request.getFlightDistance())
                .maxAltitude(request.getMaxAltitude())
                .takeoffLocation(request.getTakeoffLocation())
                .landingLocation(request.getLandingLocation())
                .weatherCondition(request.getWeatherCondition())
                .windSpeed(request.getWindSpeed())
                .batteryConsumed(request.getBatteryConsumed())
                .remarks(request.getRemarks())
                .flightDate(LocalDate.parse(request.getFlightDate(), DATE_FORMAT)).build();
        int rows = logRepo.insert(log);
        if (rows != 1) throw new com.example.drone.exception.DroneBusinessException(500, "创建飞行日志失败");
        return toDTO(logRepo.selectById(log.getId()));
    }

    @Override @Transactional(readOnly = true)
    public FlightLogDTO getFlightLogById(Long id) {
        return toDTO(logRepo.selectById(id));
    }

    @Override @Transactional(readOnly = true)
    public Page<FlightLogDTO> listFlightLogs(FlightLogQueryRequest request) {
        List<FlightLog> all = logRepo.selectAll();
        List<FlightLogDTO> dtos = all.stream().map(this::toDTO).collect(Collectors.toList());
        return Page.<FlightLogDTO>builder().total((long) dtos.size())
                .pageNum(1).pageSize(dtos.size()).pages(1).list(dtos).build();
    }

    @Override @Transactional
    public void deleteFlightLog(Long id) { logRepo.deleteById(id); }

    @Override @Transactional(readOnly = true)
    public List<FlightLogDTO> getLogsByDroneId(Long droneId) {
        return logRepo.selectByDroneId(droneId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    private FlightLogDTO toDTO(FlightLog l) {
        if (l == null) return null;
        Drone drone = droneRepo.selectById(l.getDroneId());
        return FlightLogDTO.builder().id(l.getId()).droneId(l.getDroneId())
                .droneSerialNumber(drone != null ? drone.getSerialNumber() : null)
                .missionId(l.getMissionId()).flightDuration(l.getFlightDuration())
                .flightDistance(l.getFlightDistance()).maxAltitude(l.getMaxAltitude())
                .takeoffLocation(l.getTakeoffLocation()).landingLocation(l.getLandingLocation())
                .weatherCondition(l.getWeatherCondition()).windSpeed(l.getWindSpeed())
                .batteryConsumed(l.getBatteryConsumed()).remarks(l.getRemarks())
                .flightDate(l.getFlightDate() != null ? l.getFlightDate().format(DATE_FORMAT) : null)
                .createdAt(l.getCreatedAt() != null ? l.getCreatedAt().format(DT_FORMAT) : null)
                .build();
    }
}
