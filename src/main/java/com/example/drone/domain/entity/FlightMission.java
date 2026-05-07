package com.example.drone.domain.entity;

import com.example.drone.domain.enums.MissionStatus;
import com.example.drone.domain.enums.MissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightMission {

    private Long id;
    private String missionCode;
    private Long droneId;
    private MissionType missionType;
    private MissionStatus status;
    private String pilotName;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private String flightArea;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
