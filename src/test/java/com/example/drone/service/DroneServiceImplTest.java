package com.example.drone.service;

import com.example.drone.domain.dto.CreateDroneRequest;
import com.example.drone.domain.dto.DroneDTO;
import com.example.drone.domain.dto.DroneQueryConditions;
import com.example.drone.domain.dto.DroneQueryRequest;
import com.example.drone.domain.dto.Page;
import com.example.drone.domain.dto.UpdateDroneRequest;
import com.example.drone.domain.entity.Drone;
import com.example.drone.domain.enums.DroneStatus;
import com.example.drone.exception.DroneNotFoundException;
import com.example.drone.exception.DuplicateSerialNumberException;
import com.example.drone.exception.InvalidParameterException;
import com.example.drone.repository.DroneRepository;
import com.example.drone.service.impl.DroneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * DroneServiceImpl 单元测试.
 * <p>
 * 使用 Mockito 模拟 DroneRepository 依赖，测试业务逻辑的正确性。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class DroneServiceImplTest {

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DroneServiceImpl droneService;

    private Drone testDrone;
    private CreateDroneRequest createRequest;
    private UpdateDroneRequest updateRequest;

    @BeforeEach
    void setUp() {
        testDrone = Drone.builder()
                .id(1L)
                .serialNumber("TEST-001")
                .modelName("Test Model")
                .manufacturer("Test Manufacturer")
                .purchaseDate(LocalDate.of(2024, 1, 15))
                .status(DroneStatus.AVAILABLE)
                .maxFlightTime(30)
                .maxFlightDistance(5000)
                .weight(500)
                .remarks("Test drone")
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 15, 10, 0, 0))
                .build();

        createRequest = CreateDroneRequest.builder()
                .serialNumber("TEST-001")
                .modelName("Test Model")
                .manufacturer("Test Manufacturer")
                .purchaseDate("2024-01-15")
                .status("AVAILABLE")
                .maxFlightTime(30)
                .maxFlightDistance(5000)
                .weight(500)
                .remarks("Test drone")
                .build();

        updateRequest = UpdateDroneRequest.builder()
                .status("UNDER_MAINTENANCE")
                .remarks("Updated remarks")
                .build();
    }

    @Test
    void testCreateDrone_Success() {
        // Given
        when(droneRepository.selectBySerialNumber(anyString())).thenReturn(null);
        when(droneRepository.insert(any(Drone.class))).thenAnswer(invocation -> {
            Drone drone = invocation.getArgument(0);
            drone.setId(1L);
            drone.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
            drone.setUpdatedAt(LocalDateTime.of(2024, 1, 15, 10, 0, 0));
            return 1;
        });
        when(droneRepository.selectById(1L)).thenReturn(testDrone);

        // When
        DroneDTO result = droneService.createDrone(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TEST-001", result.getSerialNumber());
        assertEquals("Test Model", result.getModelName());
        assertEquals("可用", result.getStatus());

        verify(droneRepository).selectBySerialNumber("TEST-001");
        verify(droneRepository).insert(any(Drone.class));
    }

    @Test
    void testCreateDrone_DuplicateSerialNumber() {
        // Given
        when(droneRepository.selectBySerialNumber(anyString())).thenReturn(testDrone);

        // When & Then
        assertThrows(DuplicateSerialNumberException.class, () -> {
            droneService.createDrone(createRequest);
        });

        verify(droneRepository).selectBySerialNumber("TEST-001");
        verify(droneRepository, never()).insert(any(Drone.class));
    }

    @Test
    void testCreateDrone_InvalidDateFormat() {
        // Given
        createRequest.setPurchaseDate("2024/01/15");

        // When & Then
        assertThrows(InvalidParameterException.class, () -> {
            droneService.createDrone(createRequest);
        });

        verify(droneRepository, never()).insert(any(Drone.class));
    }

    @Test
    void testCreateDrone_InvalidStatus() {
        // Given
        createRequest.setStatus("INVALID_STATUS");

        // When & Then
        assertThrows(InvalidParameterException.class, () -> {
            droneService.createDrone(createRequest);
        });

        verify(droneRepository, never()).insert(any(Drone.class));
    }

    @Test
    void testGetDroneById_Success() {
        // Given
        when(droneRepository.selectById(1L)).thenReturn(testDrone);

        // When
        DroneDTO result = droneService.getDroneById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TEST-001", result.getSerialNumber());
        assertEquals("Test Model", result.getModelName());
        assertEquals("可用", result.getStatus());

        verify(droneRepository).selectById(1L);
    }

    @Test
    void testGetDroneById_NotFound() {
        // Given
        when(droneRepository.selectById(anyLong())).thenReturn(null);

        // When & Then
        assertThrows(DroneNotFoundException.class, () -> {
            droneService.getDroneById(999L);
        });

        verify(droneRepository).selectById(999L);
    }

    @Test
    void testListDrones_WithFilters() {
        // Given
        DroneQueryRequest request = DroneQueryRequest.builder()
                .modelName("Test")
                .manufacturer("Test")
                .status("AVAILABLE")
                .pageNum(1)
                .pageSize(20)
                .build();

        List<Drone> drones = Arrays.asList(testDrone);
        when(droneRepository.selectByConditions(any(DroneQueryConditions.class))).thenReturn(drones);
        when(droneRepository.countByConditions(any(DroneQueryConditions.class))).thenReturn(1);

        // When
        Page<DroneDTO> result = droneService.listDrones(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(20, result.getPageSize());
        assertEquals(1, result.getPages());
        assertEquals(1, result.getList().size());
        assertEquals("TEST-001", result.getList().get(0).getSerialNumber());

        verify(droneRepository).selectByConditions(any(DroneQueryConditions.class));
        verify(droneRepository).countByConditions(any(DroneQueryConditions.class));
    }

    @Test
    void testListDrones_EmptyResult() {
        // Given
        DroneQueryRequest request = DroneQueryRequest.builder()
                .pageNum(1)
                .pageSize(20)
                .build();

        when(droneRepository.selectByConditions(any(DroneQueryConditions.class))).thenReturn(Collections.emptyList());
        when(droneRepository.countByConditions(any(DroneQueryConditions.class))).thenReturn(0);

        // When
        Page<DroneDTO> result = droneService.listDrones(request);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertEquals(0, result.getPages());
        assertTrue(result.getList().isEmpty());

        verify(droneRepository).selectByConditions(any(DroneQueryConditions.class));
        verify(droneRepository).countByConditions(any(DroneQueryConditions.class));
    }

    @Test
    void testListDrones_DefaultPagination() {
        // Given
        DroneQueryRequest request = new DroneQueryRequest();

        when(droneRepository.selectByConditions(any(DroneQueryConditions.class))).thenReturn(Collections.emptyList());
        when(droneRepository.countByConditions(any(DroneQueryConditions.class))).thenReturn(0);

        // When
        Page<DroneDTO> result = droneService.listDrones(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPageNum());
        assertEquals(20, result.getPageSize());

        ArgumentCaptor<DroneQueryConditions> captor = ArgumentCaptor.forClass(DroneQueryConditions.class);
        verify(droneRepository).selectByConditions(captor.capture());
        
        DroneQueryConditions conditions = captor.getValue();
        assertEquals(0, conditions.getOffset());
        assertEquals(20, conditions.getLimit());
    }

    @Test
    void testUpdateDrone_Success() {
        // Given
        when(droneRepository.selectById(1L)).thenReturn(testDrone);
        when(droneRepository.updateById(any(Drone.class))).thenReturn(1);
        
        Drone updatedDrone = Drone.builder()
                .id(1L)
                .serialNumber("TEST-001")
                .modelName("Test Model")
                .manufacturer("Test Manufacturer")
                .purchaseDate(LocalDate.of(2024, 1, 15))
                .status(DroneStatus.UNDER_MAINTENANCE)
                .maxFlightTime(30)
                .maxFlightDistance(5000)
                .weight(500)
                .remarks("Updated remarks")
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 15, 14, 0, 0))
                .build();
        
        when(droneRepository.selectById(1L)).thenReturn(testDrone, updatedDrone);

        // When
        DroneDTO result = droneService.updateDrone(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("维修中", result.getStatus());
        assertEquals("Updated remarks", result.getRemarks());

        verify(droneRepository, times(2)).selectById(1L);
        verify(droneRepository).updateById(any(Drone.class));
    }

    @Test
    void testUpdateDrone_NotFound() {
        // Given
        when(droneRepository.selectById(anyLong())).thenReturn(null);

        // When & Then
        assertThrows(DroneNotFoundException.class, () -> {
            droneService.updateDrone(999L, updateRequest);
        });

        verify(droneRepository).selectById(999L);
        verify(droneRepository, never()).updateById(any(Drone.class));
    }

    @Test
    void testUpdateDrone_DuplicateSerialNumber() {
        // Given
        UpdateDroneRequest request = UpdateDroneRequest.builder()
                .serialNumber("TEST-002")
                .build();

        Drone anotherDrone = Drone.builder()
                .id(2L)
                .serialNumber("TEST-002")
                .build();

        when(droneRepository.selectById(1L)).thenReturn(testDrone);
        when(droneRepository.selectBySerialNumber("TEST-002")).thenReturn(anotherDrone);

        // When & Then
        assertThrows(DuplicateSerialNumberException.class, () -> {
            droneService.updateDrone(1L, request);
        });

        verify(droneRepository).selectById(1L);
        verify(droneRepository).selectBySerialNumber("TEST-002");
        verify(droneRepository, never()).updateById(any(Drone.class));
    }

    @Test
    void testUpdateDrone_InvalidDateFormat() {
        // Given
        UpdateDroneRequest request = UpdateDroneRequest.builder()
                .purchaseDate("2024/01/15")
                .build();

        when(droneRepository.selectById(1L)).thenReturn(testDrone);

        // When & Then
        assertThrows(InvalidParameterException.class, () -> {
            droneService.updateDrone(1L, request);
        });

        verify(droneRepository).selectById(1L);
        verify(droneRepository, never()).updateById(any(Drone.class));
    }

    @Test
    void testUpdateDrone_InvalidStatus() {
        // Given
        UpdateDroneRequest request = UpdateDroneRequest.builder()
                .status("INVALID_STATUS")
                .build();

        when(droneRepository.selectById(1L)).thenReturn(testDrone);

        // When & Then
        assertThrows(InvalidParameterException.class, () -> {
            droneService.updateDrone(1L, request);
        });

        verify(droneRepository).selectById(1L);
        verify(droneRepository, never()).updateById(any(Drone.class));
    }

    @Test
    void testDeleteDrone_Success() {
        // Given
        when(droneRepository.selectById(1L)).thenReturn(testDrone);
        when(droneRepository.deleteById(1L)).thenReturn(1);

        // When
        droneService.deleteDrone(1L);

        // Then
        verify(droneRepository).selectById(1L);
        verify(droneRepository).deleteById(1L);
    }

    @Test
    void testDeleteDrone_NotFound() {
        // Given
        when(droneRepository.selectById(anyLong())).thenReturn(null);

        // When & Then
        assertThrows(DroneNotFoundException.class, () -> {
            droneService.deleteDrone(999L);
        });

        verify(droneRepository).selectById(999L);
        verify(droneRepository, never()).deleteById(anyLong());
    }
}
