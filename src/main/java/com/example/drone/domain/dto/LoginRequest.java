package com.example.drone.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    private String account;

    @NotBlank
    private String password;

    @NotBlank
    private String captcha;

    private Boolean rememberMe = false;
}
