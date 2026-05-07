/**
 * Interceptor 层 - 拦截器
 * 
 * <p>职责：
 * <ul>
 *   <li>HTTP 请求拦截</li>
 *   <li>请求日志记录</li>
 *   <li>性能监控</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>拦截器实现 HandlerInterceptor 接口</li>
 *   <li>拦截器使用 @Component 注解</li>
 *   <li>在 WebMvcConfig 中注册拦截器</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.interceptor;
