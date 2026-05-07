package com.example.drone.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysPermission {
    private Long id;
    private String permCode;
    private String permName;
    private String description;
}
