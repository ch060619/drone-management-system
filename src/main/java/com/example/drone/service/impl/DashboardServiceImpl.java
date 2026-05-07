package com.example.drone.service.impl;

import com.example.drone.domain.dto.*;
import com.example.drone.domain.entity.Drone;
import com.example.drone.domain.entity.FlightMission;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.MaintenanceRepository;
import com.example.drone.repository.FlightMissionRepository;
import com.example.drone.service.DashboardService;
import com.example.drone.domain.enums.MissionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    private final DroneRepository droneRepo;
    private final FlightMissionRepository missionRepo;
    private final MaintenanceRepository mtnRepo;

    public DashboardServiceImpl(DroneRepository droneRepo, FlightMissionRepository missionRepo,
                                MaintenanceRepository mtnRepo) {
        this.droneRepo = droneRepo;
        this.missionRepo = missionRepo;
        this.mtnRepo = mtnRepo;
    }

    @Override @Transactional(readOnly = true)
    public DashboardStats getStats() {
        DroneQueryConditions cond = new DroneQueryConditions();
        int totalDrones = droneRepo.countByConditions(cond);
        List<Drone> all = droneRepo.selectByConditions(cond);
        long availableCount = 0, maintenanceCount = 0, scrappedCount = 0;
        for (Drone d : all) {
            if (d.getStatus() != null) {
                switch (d.getStatus()) {
                    case AVAILABLE: availableCount++; break;
                    case UNDER_MAINTENANCE: maintenanceCount++; break;
                    case SCRAPPED: scrappedCount++; break;
                }
            }
        }
        FlightMission pendingMission = new FlightMission();
        pendingMission.setStatus(MissionStatus.PENDING);
        int activeMissions = missionRepo.countByCondition(pendingMission);

        FlightMission completedMission = new FlightMission();
        completedMission.setStatus(MissionStatus.COMPLETED);
        int completedMissions = missionRepo.countByCondition(completedMission);

        int pendingMtn = mtnRepo.countPending();

        return DashboardStats.builder()
                .totalDrones((long) totalDrones)
                .availableCount(availableCount).maintenanceCount(maintenanceCount)
                .scrappedCount(scrappedCount)
                .activeMissions((long) activeMissions).completedMissions((long) completedMissions)
                .totalFlightHours(0L).pendingMaintenance((long) pendingMtn).build();
    }
}
