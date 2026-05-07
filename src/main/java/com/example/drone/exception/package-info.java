/**
 * Exception 层 - 异常定义与处理
 * 
 * <p>职责：
 * <ul>
 *   <li>定义业务异常类</li>
 *   <li>全局异常处理器</li>
 *   <li>错误响应格式定义</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>业务异常继承自 RuntimeException</li>
 *   <li>异常类命名以 Exception 结尾</li>
 *   <li>全局异常处理器使用 @RestControllerAdvice 注解</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.exception;
