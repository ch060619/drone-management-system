/**
 * 数据传输对象（DTO）包.
 * <p>
 * 本包包含系统中所有的 DTO 类，用于在不同层之间传输数据。
 * DTO 类主要用于：
 * </p>
 * <ul>
 *   <li>API 请求参数封装（CreateDroneRequest、UpdateDroneRequest、DroneQueryRequest）</li>
 *   <li>API 响应数据封装（DroneDTO、ApiResponse、ErrorResponse）</li>
 *   <li>数据格式转换和校验</li>
 * </ul>
 * <p>
 * 所有 DTO 类使用 Lombok 注解简化代码，使用 Hibernate Validation 注解进行参数校验。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
package com.example.drone.domain.dto;
