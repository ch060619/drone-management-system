package com.example.drone.controller;

import com.example.drone.domain.dto.CreateDroneRequest;
import com.example.drone.domain.dto.DroneDTO;
import com.example.drone.domain.dto.DroneQueryRequest;
import com.example.drone.domain.dto.Page;
import com.example.drone.domain.dto.UpdateDroneRequest;
import com.example.drone.exception.DroneNotFoundException;
import com.example.drone.exception.DuplicateSerialNumberException;
import com.example.drone.service.DroneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * DroneController 单元测试.
 * <p>
 * 使用 @WebMvcTest 进行 Controller 层切片测试，验证 API 接口的行为。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@WebMvcTest(DroneController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(authorities = {"drone:list", "drone:create", "drone:update", "drone:delete"})
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DroneService droneService;

    @MockBean
    private com.example.drone.security.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.example.drone.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 测试创建无人机 - 成功场景.
     * <p>
     * 验证有效的创建请求返回 201 状态码和正确的响应体。
     * </p>
     */
    @Test
    void testCreateDrone_Success() throws Exception {
        // Given
        CreateDroneRequest request = CreateDroneRequest.builder()
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .purchaseDate("2024-01-15")
                .status("AVAILABLE")
                .maxFlightTime(46)
                .maxFlightDistance(30000)
                .weight(895)
                .remarks("企业版")
                .build();

        DroneDTO responseDto = DroneDTO.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .purchaseDate("2024-01-15")
                .status("可用")
                .maxFlightTime(46)
                .maxFlightDistance(30000)
                .weight(895)
                .remarks("企业版")
                .createdAt("2024-01-15T10:30:00Z")
                .updatedAt("2024-01-15T10:30:00Z")
                .build();

        when(droneService.createDrone(any(CreateDroneRequest.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/v1/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("创建成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.serialNumber").value("DJI-2024-001"))
                .andExpect(jsonPath("$.data.modelName").value("DJI Mavic 3"));
    }

    /**
     * 测试创建无人机 - 参数校验失败（缺少必填字段）.
     * <p>
     * 验证缺少必填字段时返回 400 状态码。
     * </p>
     */
    @Test
    void testCreateDrone_MissingRequiredField() throws Exception {
        // Given
        CreateDroneRequest request = CreateDroneRequest.builder()
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                // 缺少 serialNumber
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试创建无人机 - 序列号重复.
     * <p>
     * 验证序列号重复时返回 400 状态码和错误消息。
     * </p>
     */
    @Test
    void testCreateDrone_DuplicateSerialNumber() throws Exception {
        // Given
        CreateDroneRequest request = CreateDroneRequest.builder()
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .purchaseDate("2024-01-15")
                .status("AVAILABLE")
                .maxFlightTime(46)
                .maxFlightDistance(30000)
                .weight(895)
                .build();

        when(droneService.createDrone(any(CreateDroneRequest.class)))
                .thenThrow(new DuplicateSerialNumberException("序列号已存在: DJI-2024-001"));

        // When & Then
        mockMvc.perform(post("/api/v1/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("序列号已存在: DJI-2024-001"));
    }

    /**
     * 测试查询单个无人机 - 成功场景.
     * <p>
     * 验证查询存在的无人机返回 200 状态码和正确的响应体。
     * </p>
     */
    @Test
    void testGetDroneById_Success() throws Exception {
        // Given
        DroneDTO responseDto = DroneDTO.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .purchaseDate("2024-01-15")
                .status("可用")
                .maxFlightTime(46)
                .maxFlightDistance(30000)
                .weight(895)
                .build();

        when(droneService.getDroneById(1L)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/v1/drones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.serialNumber").value("DJI-2024-001"));
    }

    /**
     * 测试查询单个无人机 - 无人机不存在.
     * <p>
     * 验证查询不存在的无人机返回 404 状态码。
     * </p>
     */
    @Test
    void testGetDroneById_NotFound() throws Exception {
        // Given
        when(droneService.getDroneById(999L))
                .thenThrow(new DroneNotFoundException("无人机不存在，ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/v1/drones/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("无人机不存在，ID: 999"));
    }

    /**
     * 测试分页查询无人机列表 - 成功场景.
     * <p>
     * 验证分页查询返回正确的分页数据。
     * </p>
     */
    @Test
    void testListDrones_Success() throws Exception {
        // Given
        DroneDTO droneDto = DroneDTO.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .status("可用")
                .build();

        Page<DroneDTO> page = Page.<DroneDTO>builder()
                .total(1L)
                .pageNum(1)
                .pageSize(20)
                .pages(1)
                .list(Collections.singletonList(droneDto))
                .build();

        when(droneService.listDrones(any(DroneQueryRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/drones")
                        .param("pageNum", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.list[0].id").value(1));
    }

    /**
     * 测试分页查询无人机列表 - 带过滤条件.
     * <p>
     * 验证带过滤条件的查询能够正确传递参数。
     * </p>
     */
    @Test
    void testListDrones_WithFilters() throws Exception {
        // Given
        Page<DroneDTO> page = Page.<DroneDTO>builder()
                .total(0L)
                .pageNum(1)
                .pageSize(20)
                .pages(0)
                .list(Collections.emptyList())
                .build();

        when(droneService.listDrones(any(DroneQueryRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/drones")
                        .param("modelName", "Mavic")
                        .param("manufacturer", "DJI")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    /**
     * 测试更新无人机 - 成功场景.
     * <p>
     * 验证有效的更新请求返回 200 状态码和更新后的数据。
     * </p>
     */
    @Test
    void testUpdateDrone_Success() throws Exception {
        // Given
        UpdateDroneRequest request = UpdateDroneRequest.builder()
                .status("UNDER_MAINTENANCE")
                .remarks("进行例行维护检查")
                .build();

        DroneDTO responseDto = DroneDTO.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .status("维修中")
                .remarks("进行例行维护检查")
                .build();

        when(droneService.updateDrone(eq(1L), any(UpdateDroneRequest.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/v1/drones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.status").value("维修中"));
    }

    /**
     * 测试更新无人机 - 无人机不存在.
     * <p>
     * 验证更新不存在的无人机返回 404 状态码。
     * </p>
     */
    @Test
    void testUpdateDrone_NotFound() throws Exception {
        // Given
        UpdateDroneRequest request = UpdateDroneRequest.builder()
                .status("UNDER_MAINTENANCE")
                .build();

        when(droneService.updateDrone(eq(999L), any(UpdateDroneRequest.class)))
                .thenThrow(new DroneNotFoundException("无人机不存在，ID: 999"));

        // When & Then
        mockMvc.perform(put("/api/v1/drones/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * 测试删除无人机 - 成功场景.
     * <p>
     * 验证删除成功返回 200 状态码。
     * </p>
     */
    @Test
    void testDeleteDrone_Success() throws Exception {
        // Given
        doNothing().when(droneService).deleteDrone(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/drones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    /**
     * 测试删除无人机 - 无人机不存在.
     * <p>
     * 验证删除不存在的无人机返回 404 状态码。
     * </p>
     */
    @Test
    void testDeleteDrone_NotFound() throws Exception {
        // Given
        doThrow(new DroneNotFoundException("无人机不存在，ID: 999"))
                .when(droneService).deleteDrone(999L);

        // When & Then
        mockMvc.perform(delete("/api/v1/drones/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
