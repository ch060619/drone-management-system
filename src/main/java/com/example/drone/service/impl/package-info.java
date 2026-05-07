/**
 * Service 层实现类包.
 * <p>
 * 包含所有业务逻辑接口的实现类，负责：
 * <ul>
 *   <li>实现业务逻辑接口定义的方法</li>
 *   <li>执行业务规则校验</li>
 *   <li>调用 Repository 层进行数据访问</li>
 *   <li>实体对象与 DTO 之间的转换</li>
 *   <li>事务管理（使用 @Transactional 注解）</li>
 * </ul>
 * </p>
 *
 * <p>命名规范：
 * <ul>
 *   <li>实现类命名：接口名 + Impl（如 DroneServiceImpl）</li>
 *   <li>使用 @Service 注解标记为 Spring 组件</li>
 *   <li>使用构造器注入依赖（推荐）</li>
 * </ul>
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
package com.example.drone.service.impl;
