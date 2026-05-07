package com.example.drone.service.impl;

import com.example.drone.domain.dto.CreateDroneRequest;
import com.example.drone.domain.dto.DroneDTO;
import com.example.drone.domain.dto.DroneQueryConditions;
import com.example.drone.domain.dto.DroneQueryRequest;
import com.example.drone.domain.dto.Page;
import com.example.drone.domain.dto.UpdateDroneRequest;
import com.example.drone.domain.entity.Drone;
import com.example.drone.domain.enums.DroneStatus;
import com.example.drone.exception.DroneBusinessException;
import com.example.drone.exception.DroneNotFoundException;
import com.example.drone.exception.DuplicateSerialNumberException;
import com.example.drone.exception.InvalidParameterException;
import com.example.drone.repository.DroneRepository;
import com.example.drone.service.DroneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 无人机业务逻辑实现类.
 * <p>
 * 实现 DroneService 接口，提供无人机管理的核心业务逻辑。
 * 使用构造器注入 DroneRepository 依赖。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Slf4j
@Service
public class DroneServiceImpl implements DroneService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final DroneRepository droneRepository;

    /**
     * 构造函数，使用构造器注入 DroneRepository.
     *
     * @param droneRepository 无人机数据访问接口
     */
    public DroneServiceImpl(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }

    /**
     * 创建新的无人机.
     *
     * @param request 创建无人机请求 DTO
     * @return 创建成功的无人机 DTO
     * @throws DuplicateSerialNumberException 当序列号已存在时
     * @throws InvalidParameterException 当参数不符合业务规则时
     */
    @Override
    @Transactional
    public DroneDTO createDrone(CreateDroneRequest request) {
        log.info("开始创建无人机，序列号: {}", request.getSerialNumber());

        // 1. 校验序列号唯一性
        Drone existingDrone = droneRepository.selectBySerialNumber(request.getSerialNumber());
        if (existingDrone != null) {
            log.warn("序列号已存在: {}", request.getSerialNumber());
            throw new DuplicateSerialNumberException("序列号已存在: " + request.getSerialNumber());
        }

        // 2. 解析购买日期
        LocalDate purchaseDate;
        try {
            purchaseDate = LocalDate.parse(request.getPurchaseDate(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("购买日期格式错误: {}", request.getPurchaseDate(), e);
            throw new InvalidParameterException("购买日期格式错误，必须为 yyyy-MM-dd");
        }

        // 3. 解析状态枚举
        DroneStatus status;
        try {
            status = DroneStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            log.error("无效的状态值: {}", request.getStatus(), e);
            throw new InvalidParameterException("无效的状态值: " + request.getStatus());
        }

        // 4. 构建实体对象
        Drone drone = Drone.builder()
                .serialNumber(request.getSerialNumber())
                .modelName(request.getModelName())
                .manufacturer(request.getManufacturer())
                .purchaseDate(purchaseDate)
                .status(status)
                .maxFlightTime(request.getMaxFlightTime())
                .maxFlightDistance(request.getMaxFlightDistance())
                .weight(request.getWeight())
                .remarks(request.getRemarks())
                .build();

        // 5. 保存到数据库
        int rows = droneRepository.insert(drone);
        if (rows != 1) {
            log.error("插入无人机失败，受影响行数: {}", rows);
            throw new DroneBusinessException(500, "插入无人机失败");
        }

        log.info("无人机创建成功，ID: {}, 序列号: {}", drone.getId(), drone.getSerialNumber());

        // 6. 重新查询以获取数据库生成的时间戳
        Drone savedDrone = droneRepository.selectById(drone.getId());
        if (savedDrone == null) {
            log.error("查询刚创建的无人机失败，ID: {}", drone.getId());
            throw new DroneBusinessException(500, "查询刚创建的无人机失败");
        }

        // 7. 转换为 DTO 并返回
        return convertToDTO(savedDrone);
    }

    /**
     * 根据 ID 查询无人机详情.
     *
     * @param id 无人机 ID
     * @return 无人机 DTO
     * @throws DroneNotFoundException 当无人机不存在时
     */
    @Override
    @Transactional(readOnly = true)
    public DroneDTO getDroneById(Long id) {
        log.info("查询无人机详情，ID: {}", id);

        Drone drone = droneRepository.selectById(id);
        if (drone == null) {
            log.warn("无人机不存在，ID: {}", id);
            throw new DroneNotFoundException("无人机不存在，ID: " + id);
        }

        log.info("查询无人机成功，ID: {}, 序列号: {}", drone.getId(), drone.getSerialNumber());
        return convertToDTO(drone);
    }

    /**
     * 分页查询无人机列表.
     *
     * @param request 查询请求 DTO
     * @return 分页响应对象
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DroneDTO> listDrones(DroneQueryRequest request) {
        log.info("查询无人机列表，条件: {}", request);

        // 1. 构建查询条件
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        DroneQueryConditions conditions = DroneQueryConditions.builder()
                .modelName(request.getModelName())
                .manufacturer(request.getManufacturer())
                .status(request.getStatus())
                .offset(offset)
                .limit(pageSize)
                .build();

        // 2. 查询数据
        List<Drone> drones = droneRepository.selectByConditions(conditions);
        int total = droneRepository.countByConditions(conditions);

        // 3. 转换为 DTO 列表
        List<DroneDTO> droneDTOs = drones.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 4. 计算总页数
        int pages = (int) Math.ceil((double) total / pageSize);

        log.info("查询无人机列表成功，总记录数: {}, 当前页: {}, 每页数量: {}", total, pageNum, pageSize);

        // 5. 封装分页响应
        return Page.<DroneDTO>builder()
                .total((long) total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .pages(pages)
                .list(droneDTOs)
                .build();
    }

    /**
     * 更新无人机信息.
     *
     * @param id 无人机 ID
     * @param request 更新请求 DTO
     * @return 更新后的无人机 DTO
     * @throws DroneNotFoundException 当无人机不存在时
     * @throws DuplicateSerialNumberException 当新序列号已存在时
     * @throws InvalidParameterException 当参数不符合业务规则时
     */
    @Override
    @Transactional
    public DroneDTO updateDrone(Long id, UpdateDroneRequest request) {
        log.info("开始更新无人机，ID: {}", id);

        // 1. 校验无人机是否存在
        Drone existingDrone = droneRepository.selectById(id);
        if (existingDrone == null) {
            log.warn("无人机不存在，ID: {}", id);
            throw new DroneNotFoundException("无人机不存在，ID: " + id);
        }

        // 2. 如果更新了序列号，校验唯一性
        if (request.getSerialNumber() != null && !request.getSerialNumber().equals(existingDrone.getSerialNumber())) {
            Drone duplicateDrone = droneRepository.selectBySerialNumber(request.getSerialNumber());
            if (duplicateDrone != null) {
                log.warn("序列号已存在: {}", request.getSerialNumber());
                throw new DuplicateSerialNumberException("序列号已存在: " + request.getSerialNumber());
            }
        }

        // 3. 构建更新对象（只更新非空字段）
        Drone.DroneBuilder droneBuilder = Drone.builder()
                .id(id);

        if (request.getSerialNumber() != null) {
            droneBuilder.serialNumber(request.getSerialNumber());
        }
        if (request.getModelName() != null) {
            droneBuilder.modelName(request.getModelName());
        }
        if (request.getManufacturer() != null) {
            droneBuilder.manufacturer(request.getManufacturer());
        }
        if (request.getPurchaseDate() != null) {
            try {
                LocalDate purchaseDate = LocalDate.parse(request.getPurchaseDate(), DATE_FORMATTER);
                droneBuilder.purchaseDate(purchaseDate);
            } catch (DateTimeParseException e) {
                log.error("购买日期格式错误: {}", request.getPurchaseDate(), e);
                throw new InvalidParameterException("购买日期格式错误，必须为 yyyy-MM-dd");
            }
        }
        if (request.getStatus() != null) {
            try {
                DroneStatus status = DroneStatus.valueOf(request.getStatus());
                droneBuilder.status(status);
            } catch (IllegalArgumentException e) {
                log.error("无效的状态值: {}", request.getStatus(), e);
                throw new InvalidParameterException("无效的状态值: " + request.getStatus());
            }
        }
        if (request.getMaxFlightTime() != null) {
            droneBuilder.maxFlightTime(request.getMaxFlightTime());
        }
        if (request.getMaxFlightDistance() != null) {
            droneBuilder.maxFlightDistance(request.getMaxFlightDistance());
        }
        if (request.getWeight() != null) {
            droneBuilder.weight(request.getWeight());
        }
        if (request.getRemarks() != null) {
            droneBuilder.remarks(request.getRemarks());
        }

        Drone drone = droneBuilder.build();

        // 4. 更新数据库
        int rows = droneRepository.updateById(drone);
        if (rows != 1) {
            log.error("更新无人机失败，受影响行数: {}", rows);
            throw new DroneBusinessException(500, "更新无人机失败");
        }

        log.info("无人机更新成功，ID: {}", id);

        // 5. 查询更新后的数据并返回
        Drone updatedDrone = droneRepository.selectById(id);
        return convertToDTO(updatedDrone);
    }

    /**
     * 删除无人机.
     *
     * @param id 无人机 ID
     * @throws DroneNotFoundException 当无人机不存在时
     */
    @Override
    @Transactional
    public void deleteDrone(Long id) {
        log.info("开始删除无人机，ID: {}", id);

        // 1. 校验无人机是否存在
        Drone existingDrone = droneRepository.selectById(id);
        if (existingDrone == null) {
            log.warn("无人机不存在，ID: {}", id);
            throw new DroneNotFoundException("无人机不存在，ID: " + id);
        }

        // 2. 删除数据
        int rows = droneRepository.deleteById(id);
        if (rows != 1) {
            log.error("删除无人机失败，受影响行数: {}", rows);
            throw new DroneBusinessException(500, "删除无人机失败");
        }

        log.info("无人机删除成功，ID: {}", id);
    }

    /**
     * 将 Drone 实体转换为 DroneDTO.
     *
     * @param drone 无人机实体
     * @return 无人机 DTO
     */
    private DroneDTO convertToDTO(Drone drone) {
        return DroneDTO.builder()
                .id(drone.getId())
                .serialNumber(drone.getSerialNumber())
                .modelName(drone.getModelName())
                .manufacturer(drone.getManufacturer())
                .purchaseDate(drone.getPurchaseDate().format(DATE_FORMATTER))
                .status(drone.getStatus().getDescription())
                .maxFlightTime(drone.getMaxFlightTime())
                .maxFlightDistance(drone.getMaxFlightDistance())
                .weight(drone.getWeight())
                .remarks(drone.getRemarks())
                .createdAt(drone.getCreatedAt() != null ? drone.getCreatedAt().format(DATETIME_FORMATTER) : null)
                .updatedAt(drone.getUpdatedAt() != null ? drone.getUpdatedAt().format(DATETIME_FORMATTER) : null)
                .build();
    }
}
