package com.example.drone.domain.dto;

import com.example.drone.domain.enums.MissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMissionRequest {

    @NotNull(message = "无人机ID不能为空")
    private Long droneId;

    @NotNull(message = "任务类型不能为空")
    private MissionType missionType;

    @NotBlank(message = "操作员不能为空")
    private String pilotName;

    private String plannedStartTime;
    private String plannedEndTime;
    private String flightArea;
    private String remarks;
}
