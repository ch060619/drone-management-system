package com.example.drone.controller;

import com.example.drone.domain.dto.DroneDTO;
import com.example.drone.domain.dto.Page;
import com.example.drone.service.DroneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DroneViewController 单元测试.
 * <p>
 * 测试前端视图控制器的所有页面路由方法。
 * 使用 Mockito 模拟 DroneService 和 Model 对象。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class DroneViewControllerTest {

    @Mock
    private DroneService droneService;

    @Mock
    private Model model;

    @InjectMocks
    private DroneViewController droneViewController;

    private DroneDTO testDrone;
    private Page<DroneDTO> testPage;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testDrone = new DroneDTO();
        testDrone.setId(1L);
        testDrone.setSerialNumber("TEST-001");
        testDrone.setModelName("Test Model");
        testDrone.setManufacturer("Test Manufacturer");
        testDrone.setPurchaseDate("2024-01-15");
        testDrone.setStatus("可用");
        testDrone.setMaxFlightTime(30);
        testDrone.setMaxFlightDistance(5000);
        testDrone.setWeight(500);

        List<DroneDTO> drones = new ArrayList<>();
        drones.add(testDrone);

        testPage = new Page<>();
        testPage.setList(drones);
        testPage.setTotal(1L);
        testPage.setPageNum(1);
        testPage.setPageSize(20);
        testPage.setPages(1);
    }

    /**
     * 测试显示无人机列表页面 - 无查询条件.
     */
    @Test
    void testListDrones_NoFilters() {
        // 模拟 Service 返回
        when(droneService.listDrones(any())).thenReturn(testPage);

        // 调用控制器方法
        String viewName = droneViewController.listDrones(null, null, null, 1, 20, model);

        // 验证返回的视图名称
        assertEquals("list", viewName);

        // 验证 Model 属性
        verify(model).addAttribute(eq("drones"), eq(testPage));
        verify(model).addAttribute(eq("queryRequest"), any());
    }

    /**
     * 测试显示无人机列表页面 - 带查询条件.
     */
    @Test
    void testListDrones_WithFilters() {
        // 模拟 Service 返回
        when(droneService.listDrones(any())).thenReturn(testPage);

        // 调用控制器方法
        String viewName = droneViewController.listDrones("Mavic", "DJI", "AVAILABLE", 1, 10, model);

        // 验证返回的视图名称
        assertEquals("list", viewName);

        // 验证 Model 属性
        verify(model).addAttribute(eq("drones"), eq(testPage));
        verify(model).addAttribute(eq("queryRequest"), any());
    }

    /**
     * 测试显示无人机详情页面.
     */
    @Test
    void testGetDroneDetail() {
        // 模拟 Service 返回
        when(droneService.getDroneById(1L)).thenReturn(testDrone);

        // 调用控制器方法
        String viewName = droneViewController.getDroneDetail(1L, model);

        // 验证返回的视图名称
        assertEquals("detail", viewName);

        // 验证 Model 属性
        verify(model).addAttribute("drone", testDrone);
    }

    /**
     * 测试显示创建无人机表单页面.
     */
    @Test
    void testShowCreateForm() {
        // 调用控制器方法
        String viewName = droneViewController.showCreateForm(model);

        // 验证返回的视图名称
        assertEquals("form", viewName);

        // 验证 Model 属性
        verify(model).addAttribute("isEdit", false);
    }

    /**
     * 测试显示编辑无人机表单页面.
     */
    @Test
    void testShowEditForm() {
        // 模拟 Service 返回
        when(droneService.getDroneById(1L)).thenReturn(testDrone);

        // 调用控制器方法
        String viewName = droneViewController.showEditForm(1L, model);

        // 验证返回的视图名称
        assertEquals("form", viewName);

        // 验证 Model 属性
        verify(model).addAttribute("isEdit", true);
        verify(model).addAttribute("drone", testDrone);
    }
}
