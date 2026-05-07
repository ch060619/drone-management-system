package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardStats {
    private Long totalDrones;
    private Long availableCount;
    private Long maintenanceCount;
    private Long scrappedCount;
    private Long activeMissions;
    private Long completedMissions;
    private Long totalFlightHours;
    private Long pendingMaintenance;
}
