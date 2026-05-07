/**
 * Service 层 - 业务逻辑层
 * 
 * <p>职责：
 * <ul>
 *   <li>实现核心业务逻辑</li>
 *   <li>执行业务规则校验</li>
 *   <li>协调多个 Repository 操作</li>
 *   <li>实体与 DTO 转换</li>
 *   <li>事务管理</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>接口与实现分离（接口放在 service 包，实现放在 service.impl 包）</li>
 *   <li>使用 @Transactional 注解管理事务</li>
 *   <li>使用构造器注入依赖</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.service;
