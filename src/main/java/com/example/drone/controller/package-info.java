/**
 * Controller 层 - REST 控制器
 * 
 * <p>职责：
 * <ul>
 *   <li>处理 HTTP 请求和响应</li>
 *   <li>参数校验（使用 @Valid 注解）</li>
 *   <li>调用 Service 层执行业务逻辑</li>
 *   <li>返回统一格式的响应</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>不得直接依赖 Repository 层</li>
 *   <li>不得包含业务逻辑</li>
 *   <li>使用构造器注入依赖</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.controller;
