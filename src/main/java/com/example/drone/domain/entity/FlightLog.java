package com.example.drone.domain.entity;

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
public class FlightLog {

    private Long id;
    private Long droneId;
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
    private LocalDate flightDate;
    private LocalDateTime createdAt;
}
