package com.example.drone.service;

import com.example.drone.domain.dto.LoginRequest;
import com.example.drone.domain.dto.LoginResponse;
import com.example.drone.domain.dto.RefreshTokenRequest;
import com.example.drone.domain.dto.RegisterRequest;
import com.example.drone.domain.entity.SysUser;
import com.example.drone.exception.DroneBusinessException;
import com.example.drone.repository.LoginLogRepository;
import com.example.drone.repository.PermissionRepository;
import com.example.drone.repository.RoleRepository;
import com.example.drone.repository.UserRepository;
import com.example.drone.security.JwtTokenProvider;
import com.example.drone.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private LoginLogRepository loginLogRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "mailFrom", "noreply@drone.com");
        ReflectionTestUtils.setField(userService, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(userService, "mailSender", mailSender);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(mailSender).send((org.springframework.mail.SimpleMailMessage) any());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(redisTemplate.hasKey("verify_code_cooldown:test@test.com")).thenReturn(false);
        when(valueOps.get("verify_code:test@test.com")).thenReturn("123456");
        when(userRepository.selectByEmail("test@test.com")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");

        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setPassword("Abc@12345");
        req.setVerifyCode("123456");

        assertDoesNotThrow(() -> userService.register(req));
        verify(userRepository).insert(any(SysUser.class));
    }

    @Test
    void shouldRejectRegistrationWithWrongVerifyCode() {
        when(valueOps.get("verify_code:test@test.com")).thenReturn("654321");

        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setPassword("Abc@12345");
        req.setVerifyCode("123456");

        DroneBusinessException ex = assertThrows(DroneBusinessException.class, () -> userService.register(req));
        assertTrue(ex.getMessage().contains("验证码错误"));
    }

    @Test
    void shouldRejectRegistrationWithExpiredVerifyCode() {
        when(valueOps.get("verify_code:test@test.com")).thenReturn(null);

        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setPassword("Abc@12345");
        req.setVerifyCode("123456");

        DroneBusinessException ex = assertThrows(DroneBusinessException.class, () -> userService.register(req));
        assertTrue(ex.getMessage().contains("已过期"));
    }

    @Test
    void shouldLoginSuccessfully() {
        when(redisTemplate.hasKey("login_lock:test@test.com")).thenReturn(false);
        SysUser user = SysUser.builder().id(1L).email("test@test.com")
                .passwordHash("$2a$10$hashed").status("ACTIVE").role("user").build();
        when(userRepository.selectByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("Abc@12345", "$2a$10$hashed")).thenReturn(true);
        when(permissionRepository.selectPermCodesByUserId(1L)).thenReturn(java.util.Arrays.asList("drone:list"));
        when(jwtTokenProvider.generateAccessToken(eq(1L), anyList(), anyList())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(eq(1L), eq(false))).thenReturn("refresh-token");

        LoginRequest req = new LoginRequest();
        req.setAccount("test@test.com");
        req.setPassword("Abc@12345");
        req.setCaptcha("dev");
        req.setRememberMe(false);

        LoginResponse resp = userService.login(req, "127.0.0.1", "Chrome");
        assertNotNull(resp);
        assertEquals("access-token", resp.getAccessToken());
        assertEquals("refresh-token", resp.getRefreshToken());
        assertFalse(Boolean.TRUE.equals(resp.getMfaRequired()));
    }

    @Test
    void shouldRejectLoginWithWrongPassword() {
        when(redisTemplate.hasKey("login_lock:test@test.com")).thenReturn(false);
        SysUser user = SysUser.builder().id(1L).email("test@test.com")
                .passwordHash("$2a$10$hashed").status("ACTIVE").role("user").build();
        when(userRepository.selectByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "$2a$10$hashed")).thenReturn(false);

        LoginRequest req = new LoginRequest();
        req.setAccount("test@test.com");
        req.setPassword("wrong");
        req.setCaptcha("dev");

        DroneBusinessException ex = assertThrows(DroneBusinessException.class, () -> userService.login(req, "127.0.0.1", "Chrome"));
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void shouldRejectLoginForInactiveUser() {
        when(redisTemplate.hasKey("login_lock:test@test.com")).thenReturn(false);
        SysUser user = SysUser.builder().id(1L).email("test@test.com")
                .passwordHash("$2a$10$hashed").status("INACTIVE").role("user").build();
        when(userRepository.selectByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("Abc@12345", "$2a$10$hashed")).thenReturn(true);

        LoginRequest req = new LoginRequest();
        req.setAccount("test@test.com");
        req.setPassword("Abc@12345");
        req.setCaptcha("dev");

        DroneBusinessException ex = assertThrows(DroneBusinessException.class, () -> userService.login(req, "127.0.0.1", "Chrome"));
        assertTrue(ex.getMessage().contains("未激活"));
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        when(jwtTokenProvider.validateToken("old-refresh")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("old-refresh")).thenReturn(1L);
        when(valueOps.get("refresh_token:1")).thenReturn("old-refresh");
        SysUser user = SysUser.builder().id(1L).email("test@test.com").role("user").build();
        when(userRepository.selectById(1L)).thenReturn(user);
        when(permissionRepository.selectPermCodesByUserId(1L)).thenReturn(java.util.Arrays.asList("drone:list"));
        when(jwtTokenProvider.generateAccessToken(eq(1L), anyList(), anyList())).thenReturn("new-access");
        when(jwtTokenProvider.generateRefreshToken(eq(1L), eq(false))).thenReturn("new-refresh");

        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken("old-refresh");

        LoginResponse resp = userService.refreshToken(req);
        assertEquals("new-access", resp.getAccessToken());
        assertEquals("new-refresh", resp.getRefreshToken());
    }
}
