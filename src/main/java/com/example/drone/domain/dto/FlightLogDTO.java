package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FlightLogDTO {
    private Long id;
    private Long droneId;
    private String droneSerialNumber;
    private Long missionId;
    private Integer flightDuration;
    private Integer flightDistance;
    private Integer maxAltitude;
    private String takeoffLocation;
    private String landingLocation;
    private String weatherCondition;
    private Integer windSpeed;
    private Integer batteryConsumed;
    private String remarks;
    private String flightDate;
    private String createdAt;
}
