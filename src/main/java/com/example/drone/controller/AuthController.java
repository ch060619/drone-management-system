package com.example.drone.controller;

import com.example.drone.domain.dto.*;
import com.example.drone.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/send-verify-code")
    public ResponseEntity<ApiResponse<?>> sendVerifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        String code = userService.sendVerifyCode(request.getEmail());
        if (code != null) {
            java.util.Map<String, String> map = new java.util.HashMap<>();
            map.put("code", code);
            return ResponseEntity.ok(ApiResponse.success("验证码已生成（开发模式）", map));
        }
        return ResponseEntity.ok(ApiResponse.message("验证码已发送至邮箱"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.message("注册成功，请查收激活邮件"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request,
                                                             HttpServletRequest httpRequest,
                                                             HttpServletResponse httpResponse) {
        String ip = httpRequest.getRemoteAddr();
        String device = httpRequest.getHeader("User-Agent");
        LoginResponse response = userService.login(request, ip, device);
        if (response.getRefreshToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                    .httpOnly(true).secure(false).path("/").sameSite("Strict").maxAge(604800).build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = userService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            userService.logout(userId);
        }
        return ResponseEntity.ok(ApiResponse.message("已登出"));
    }

    @GetMapping("/activate")
    public ResponseEntity<ApiResponse<Void>> activate(@RequestParam("token") String token) {
        userService.activateAccount(token);
        return ResponseEntity.ok(ApiResponse.message("账号已激活，请登录"));
    }

    @PostMapping("/mfa-verify")
    public ResponseEntity<ApiResponse<LoginResponse>> mfaVerify(@RequestBody java.util.Map<String, String> body) {
        String mfaToken = body.get("mfaToken");
        String code = body.get("code");
        LoginResponse response = userService.verifyMfa(mfaToken, code);
        return ResponseEntity.ok(ApiResponse.success("MFA验证通过", response));
    }

    @PostMapping("/delete-account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@RequestBody java.util.Map<String, String> body) {
        Long userId = getCurrentUserId();
        String code = body.get("code");
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.message("请输入验证码"));
        }
        userService.deleteAccount(userId, code);
        return ResponseEntity.ok(ApiResponse.message("账号已注销"));
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return null;
    }
}
