package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MaintenanceQueryRequest {
    private Long droneId;
    private String status;
    @Builder.Default
    private Integer pageNum = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
