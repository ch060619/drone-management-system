package com.example.drone.controller;

import com.example.drone.domain.dto.ApiResponse;
import com.example.drone.domain.dto.BatchStatusRequest;
import com.example.drone.domain.dto.CreateDroneRequest;
import com.example.drone.domain.dto.DroneDTO;
import com.example.drone.domain.dto.DroneQueryRequest;
import com.example.drone.domain.dto.Page;
import com.example.drone.domain.dto.UpdateDroneRequest;
import com.example.drone.service.DroneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 无人机管理控制器.
 * <p>
 * 提供无人机信息的 RESTful API 接口，包括创建、查询、更新和删除操作。
 * 所有接口均遵循统一的响应格式（ApiResponse）。
 * </p>
 * 
 * <p>基础路径：/api/v1/drones</p>
 * 
 * <p>接口列表：
 * <ul>
 *   <li>POST /api/v1/drones - 创建新无人机</li>
 *   <li>GET /api/v1/drones/{id} - 查询单个无人机</li>
 *   <li>GET /api/v1/drones - 分页查询无人机列表</li>
 *   <li>PUT /api/v1/drones/{id} - 更新无人机信息</li>
 *   <li>DELETE /api/v1/drones/{id} - 删除无人机</li>
 * </ul>
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/drones")
public class DroneController {

    private final DroneService droneService;

    /**
     * 构造函数注入 DroneService.
     *
     * @param droneService 无人机业务逻辑服务
     */
    @Autowired
    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    /**
     * 创建新无人机.
     * <p>
     * 接收创建请求，验证参数后保存到数据库。
     * 序列号必须唯一，所有必填字段必须提供。
     * </p>
     * 
     * <p>请求示例：
     * <pre>
     * POST /api/v1/drones
     * Content-Type: application/json
     * 
     * {
     *   "serialNumber": "DJI-2024-001",
     *   "modelName": "DJI Mavic 3",
     *   "manufacturer": "DJI",
     *   "purchaseDate": "2024-01-15",
     *   "status": "AVAILABLE",
     *   "maxFlightTime": 46,
     *   "maxFlightDistance": 30000,
     *   "weight": 895,
     *   "remarks": "企业版"
     * }
     * </pre>
     * </p>
     *
     * @param request 创建无人机请求 DTO
     * @return 创建成功的无人机信息（HTTP 201）
     */
    @PostMapping
    @PreAuthorize("hasAuthority('drone:create')")
    public ResponseEntity<ApiResponse<DroneDTO>> createDrone(@Valid @RequestBody CreateDroneRequest request) {
        log.info("创建无人机请求: serialNumber={}, modelName={}", request.getSerialNumber(), request.getModelName());
        DroneDTO drone = droneService.createDrone(request);
        log.info("无人机创建成功: id={}, serialNumber={}", drone.getId(), drone.getSerialNumber());
        return ResponseEntity.status(201)
                .body(ApiResponse.created(drone));
    }

    /**
     * 根据 ID 查询无人机详情.
     * <p>
     * 根据无人机 ID 查询完整的无人机信息。
     * 如果无人机不存在，返回 404 错误。
     * </p>
     * 
     * <p>请求示例：
     * <pre>
     * GET /api/v1/drones/1
     * </pre>
     * </p>
     *
     * @param id 无人机 ID
     * @return 无人机详情（HTTP 200）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('drone:list')")
    public ResponseEntity<ApiResponse<DroneDTO>> getDroneById(@PathVariable Long id) {
        log.info("查询无人机详情: id={}", id);
        DroneDTO drone = droneService.getDroneById(id);
        log.info("查询成功: serialNumber={}", drone.getSerialNumber());
        return ResponseEntity.ok(ApiResponse.success(drone));
    }

    /**
     * 分页查询无人机列表.
     * <p>
     * 支持按型号、制造商、状态进行过滤，支持分页。
     * 所有查询参数均为可选。
     * </p>
     * 
     * <p>请求示例：
     * <pre>
     * GET /api/v1/drones?modelName=Mavic&manufacturer=DJI&status=AVAILABLE&pageNum=1&pageSize=20
     * </pre>
     * </p>
     *
     * @param request 查询请求 DTO（包含过滤条件和分页参数）
     * @return 分页查询结果（HTTP 200）
     */
    @GetMapping
    @PreAuthorize("hasAuthority('drone:list')")
    public ResponseEntity<ApiResponse<Page<DroneDTO>>> listDrones(DroneQueryRequest request) {
        log.info("查询无人机列表: modelName={}, manufacturer={}, status={}, pageNum={}, pageSize={}",
                request.getModelName(), request.getManufacturer(), request.getStatus(),
                request.getPageNum(), request.getPageSize());
        Page<DroneDTO> page = droneService.listDrones(request);
        log.info("查询成功: total={}, pageNum={}, pageSize={}", page.getTotal(), page.getPageNum(), page.getPageSize());
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    /**
     * 更新无人机信息.
     * <p>
     * 根据 ID 更新无人机信息，支持部分更新（只更新提供的字段）。
     * 如果更新序列号，新序列号必须唯一。
     * </p>
     * 
     * <p>请求示例：
     * <pre>
     * PUT /api/v1/drones/1
     * Content-Type: application/json
     * 
     * {
     *   "status": "UNDER_MAINTENANCE",
     *   "remarks": "进行例行维护检查"
     * }
     * </pre>
     * </p>
     *
     * @param id 无人机 ID
     * @param request 更新请求 DTO
     * @return 更新后的无人机信息（HTTP 200）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('drone:update')")
    public ResponseEntity<ApiResponse<DroneDTO>> updateDrone(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDroneRequest request) {
        log.info("更新无人机请求: id={}", id);
        DroneDTO drone = droneService.updateDrone(id, request);
        log.info("无人机更新成功: id={}, serialNumber={}", drone.getId(), drone.getSerialNumber());
        return ResponseEntity.ok(ApiResponse.success("更新成功", drone));
    }

    /**
     * 删除无人机.
     * <p>
     * 根据 ID 删除无人机记录。
     * 删除操作不可恢复，建议前端进行二次确认。
     * </p>
     * 
     * <p>请求示例：
     * <pre>
     * DELETE /api/v1/drones/1
     * </pre>
     * </p>
     *
     * @param id 无人机 ID
     * @return 删除成功响应（HTTP 200）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('drone:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteDrone(@PathVariable Long id) {
        log.info("删除无人机请求: id={}", id);
        droneService.deleteDrone(id);
        log.info("无人机删除成功: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PutMapping("/batch-status")
    public ResponseEntity<ApiResponse<Void>> batchUpdateStatus(@Valid @RequestBody BatchStatusRequest request) {
        log.info("批量更新无人机状态: ids={}, status={}", request.getIds(), request.getStatus());
        for (Long id : request.getIds()) {
            UpdateDroneRequest updateReq = new UpdateDroneRequest();
            updateReq.setStatus(request.getStatus());
            droneService.updateDrone(id, updateReq);
        }
        return ResponseEntity.ok(ApiResponse.success("批量更新成功", null));
    }
}
