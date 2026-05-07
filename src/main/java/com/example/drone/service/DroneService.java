package com.example.drone.service;

import com.example.drone.domain.dto.CreateDroneRequest;
import com.example.drone.domain.dto.DroneDTO;
import com.example.drone.domain.dto.DroneQueryRequest;
import com.example.drone.domain.dto.Page;
import com.example.drone.domain.dto.UpdateDroneRequest;

/**
 * 无人机业务逻辑接口.
 * <p>
 * 定义无人机管理的核心业务方法，包括创建、查询、更新和删除操作。
 * 所有方法均遵循以下约定：
 * <ul>
 *   <li>参数校验：在 Service 层进行业务规则校验</li>
 *   <li>异常处理：抛出具体的业务异常（DroneNotFoundException、DuplicateSerialNumberException 等）</li>
 *   <li>事务管理：写操作使用 @Transactional 注解</li>
 *   <li>DTO 转换：实体对象与 DTO 之间的转换在 Service 层完成</li>
 * </ul>
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
public interface DroneService {

    /**
     * 创建新的无人机.
     * <p>
     * 业务逻辑：
     * <ol>
     *   <li>校验序列号唯一性（如果序列号已存在，抛出 DuplicateSerialNumberException）</li>
     *   <li>将请求 DTO 转换为实体对象</li>
     *   <li>保存到数据库</li>
     *   <li>将保存后的实体对象转换为响应 DTO 并返回</li>
     * </ol>
     * </p>
     *
     * @param request 创建无人机请求 DTO，包含所有必填字段
     * @return 创建成功的无人机 DTO，包含自动生成的 ID 和时间戳
     * @throws com.example.drone.exception.DuplicateSerialNumberException 当序列号已存在时
     * @throws com.example.drone.exception.InvalidParameterException 当参数不符合业务规则时
     */
    DroneDTO createDrone(CreateDroneRequest request);

    /**
     * 根据 ID 查询无人机详情.
     * <p>
     * 业务逻辑：
     * <ol>
     *   <li>根据 ID 从数据库查询无人机实体</li>
     *   <li>如果不存在，抛出 DroneNotFoundException</li>
     *   <li>将实体对象转换为响应 DTO 并返回</li>
     * </ol>
     * </p>
     *
     * @param id 无人机 ID
     * @return 无人机 DTO
     * @throws com.example.drone.exception.DroneNotFoundException 当无人机不存在时
     */
    DroneDTO getDroneById(Long id);

    /**
     * 分页查询无人机列表.
     * <p>
     * 业务逻辑：
     * <ol>
     *   <li>将查询请求 DTO 转换为查询条件对象</li>
     *   <li>计算分页参数（offset 和 limit）</li>
     *   <li>查询符合条件的无人机列表和总记录数</li>
     *   <li>将实体列表转换为 DTO 列表</li>
     *   <li>封装为分页响应对象并返回</li>
     * </ol>
     * </p>
     *
     * @param request 查询请求 DTO，包含查询条件和分页参数
     * @return 分页响应对象，包含无人机 DTO 列表和分页信息
     */
    Page<DroneDTO> listDrones(DroneQueryRequest request);

    /**
     * 更新无人机信息.
     * <p>
     * 业务逻辑：
     * <ol>
     *   <li>校验无人机是否存在（如果不存在，抛出 DroneNotFoundException）</li>
     *   <li>如果更新了序列号，校验新序列号的唯一性（如果重复，抛出 DuplicateSerialNumberException）</li>
     *   <li>将更新请求 DTO 的非空字段应用到实体对象</li>
     *   <li>更新数据库</li>
     *   <li>查询更新后的实体对象，转换为响应 DTO 并返回</li>
     * </ol>
     * </p>
     *
     * @param id 无人机 ID
     * @param request 更新请求 DTO，只更新提供的字段
     * @return 更新后的无人机 DTO
     * @throws com.example.drone.exception.DroneNotFoundException 当无人机不存在时
     * @throws com.example.drone.exception.DuplicateSerialNumberException 当新序列号已存在时
     * @throws com.example.drone.exception.InvalidParameterException 当参数不符合业务规则时
     */
    DroneDTO updateDrone(Long id, UpdateDroneRequest request);

    /**
     * 删除无人机.
     * <p>
     * 业务逻辑：
     * <ol>
     *   <li>校验无人机是否存在（如果不存在，抛出 DroneNotFoundException）</li>
     *   <li>从数据库中删除无人机记录</li>
     * </ol>
     * </p>
     *
     * @param id 无人机 ID
     * @throws com.example.drone.exception.DroneNotFoundException 当无人机不存在时
     */
    void deleteDrone(Long id);
}
