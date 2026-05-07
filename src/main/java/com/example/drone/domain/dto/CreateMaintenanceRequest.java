package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateMaintenanceRequest {
    @NotNull(message = "无人机ID不能为空")
    private Long droneId;
    @NotBlank(message = "维护类型不能为空")
    private String maintenanceType;
    @NotBlank(message = "计划维护日期不能为空")
    private String scheduledDate;
    private String technician;
    private String description;
}
