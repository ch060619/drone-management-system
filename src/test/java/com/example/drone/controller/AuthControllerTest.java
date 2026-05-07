package com.example.drone.controller;

import com.example.drone.domain.dto.*;
import com.example.drone.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private com.example.drone.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.example.drone.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldSendVerifyCode() throws Exception {
        when(userService.sendVerifyCode("test@test.com")).thenReturn(null);

        mockMvc.perform(post("/api/v1/auth/send-verify-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("验证码已发送至邮箱"));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        doNothing().when(userService).register(any());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@test.com\",\"password\":\"Abc@12345\",\"verifyCode\":\"123456\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("注册成功，请查收激活邮件"));
    }

    @Test
    void shouldRejectRegisterWithMissingFields() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"password\":\"\",\"verifyCode\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginResponse resp = LoginResponse.builder()
                .accessToken("at").refreshToken("rt").expiresIn(7200L)
                .userInfo(UserInfoDTO.builder().id(1L).email("admin@drone.com")
                        .roles(Collections.singletonList("admin"))
                        .permissions(Collections.singletonList("drone:create")).build())
                .mfaRequired(false).build();
        when(userService.login(any(), anyString(), nullable(String.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"account\":\"admin@drone.com\",\"password\":\"admin123\",\"captcha\":\"dev\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("at"))
                .andExpect(jsonPath("$.data.mfaRequired").value(false));
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        LoginResponse resp = LoginResponse.builder()
                .accessToken("new-at").refreshToken("new-rt").expiresIn(7200L)
                .userInfo(UserInfoDTO.builder().id(1L).build()).mfaRequired(false).build();
        when(userService.refreshToken(any())).thenReturn(resp);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"old-rt\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-at"));
    }

    @Test
    void shouldActivateAccount() throws Exception {
        doNothing().when(userService).activateAccount("valid-token");

        mockMvc.perform(get("/api/v1/auth/activate").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("账号已激活，请登录"));
    }
}
