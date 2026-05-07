package com.example.drone.service;

import com.example.drone.domain.dto.LoginRequest;
import com.example.drone.domain.dto.LoginResponse;
import com.example.drone.domain.dto.RegisterRequest;
import com.example.drone.domain.dto.RefreshTokenRequest;

public interface UserService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request, String ip, String device);
    LoginResponse refreshToken(RefreshTokenRequest request);
    LoginResponse verifyMfa(String mfaToken, String code);
    void logout(Long userId);
    String sendVerifyCode(String email);
    void activateAccount(String token);
    void deleteAccount(Long userId, String code);
}
