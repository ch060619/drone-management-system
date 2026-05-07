package com.example.drone.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    private Long id;
    private String email;
    private String passwordHash;
    private String status;
    private String role;
    private String activationToken;
    private String createdAt;
    private String updatedAt;
}
