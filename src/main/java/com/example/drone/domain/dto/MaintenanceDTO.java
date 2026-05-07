package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MaintenanceDTO {
    private Long id;
    private Long droneId;
    private String droneSerialNumber;
    private String maintenanceType;
    private String status;
    private String scheduledDate;
    private String completedDate;
    private String technician;
    private String partsReplaced;
    private Integer cost;
    private String description;
    private String nextMaintenanceDate;
    private String createdAt;
    private String updatedAt;
}
