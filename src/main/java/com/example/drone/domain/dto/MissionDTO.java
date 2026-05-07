package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionDTO {
    private Long id;
    private String missionCode;
    private Long droneId;
    private String droneSerialNumber;
    private String missionType;
    private String status;
    private String pilotName;
    private String plannedStartTime;
    private String plannedEndTime;
    private String actualStartTime;
    private String actualEndTime;
    private String flightArea;
    private String remarks;
    private String createdAt;
    private String updatedAt;
}
