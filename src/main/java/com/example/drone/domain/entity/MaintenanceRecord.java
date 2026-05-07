package com.example.drone.domain.entity;

import com.example.drone.domain.enums.MaintenanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {

    private Long id;
    private Long droneId;
    private MaintenanceType maintenanceType;
    private String status;
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private String technician;
    private String partsReplaced;
    private Integer cost;
    private String description;
    private LocalDate nextMaintenanceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
