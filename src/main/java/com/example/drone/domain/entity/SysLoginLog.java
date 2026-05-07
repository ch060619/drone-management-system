package com.example.drone.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysLoginLog {
    private Long id;
    private Long userId;
    private String ip;
    private String device;
    private String loginTime;
    private String result;
    private String failReason;
}
