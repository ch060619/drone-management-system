package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionQueryRequest {
    private String missionType;
    private String status;
    private Long droneId;
    private String pilotName;
    private String startFrom;
    private String startTo;
    private String endFrom;
    private String endTo;
    @Builder.Default
    private Integer pageNum = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
