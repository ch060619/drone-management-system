/**
 * Domain 层 - 领域模型层
 * 
 * <p>职责：
 * <ul>
 *   <li>定义领域实体（entity 子包）</li>
 *   <li>定义数据传输对象 DTO（dto 子包）</li>
 *   <li>定义枚举类型（enums 子包）</li>
 *   <li>定义值对象（如需要）</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>不得包含业务逻辑</li>
 *   <li>不得依赖 Spring 框架注解（除 JPA 和校验注解外）</li>
 *   <li>实体类使用 Lombok 简化代码</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.domain;
