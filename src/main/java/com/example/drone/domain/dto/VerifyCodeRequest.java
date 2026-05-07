package com.example.drone.domain.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class VerifyCodeRequest {
    @Email
    @NotBlank
    private String email;
}
