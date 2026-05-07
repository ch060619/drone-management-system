package com.example.drone.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysOperationLog {
    private Long id;
    private Long userId;
    private String operation;
    private String target;
    private String ip;
    private String opTime;
    private String result;
}
