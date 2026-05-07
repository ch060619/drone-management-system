package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateFlightLogRequest {
    @NotNull(message = "无人机ID不能为空")
    private Long droneId;
    private Long missionId;
    @NotNull(message = "飞行时长不能为空")
    private Integer flightDuration;
    private Integer flightDistance;
    private Integer maxAltitude;
    private String takeoffLocation;
    private String landingLocation;
    private String weatherCondition;
    private Integer windSpeed;
    private Integer batteryConsumed;
    private String remarks;
    @NotBlank(message = "飞行日期不能为空")
    private String flightDate;
}
