package com.example.drone.exception;

/**
 * 未授权异常
 * 
 * <p>当用户未通过身份认证就尝试访问需要认证的资源时抛出此异常。
 * 对应 HTTP 状态码 401 Unauthorized。
 * 
 * <p>使用场景：
 * <ul>
 *   <li>用户未登录就访问需要认证的 API</li>
 *   <li>Token 过期或无效</li>
 *   <li>认证信息缺失</li>
 * </ul>
 * 
 * <p>示例：
 * <pre>
 * if (currentUser == null) {
 *     throw new UnauthorizedException("用户未登录");
 * }
 * </pre>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
public class UnauthorizedException extends DroneBusinessException {

    /**
     * HTTP 状态码 401
     */
    private static final int ERROR_CODE = 401;

    /**
     * 构造未授权异常
     * 
     * @param message 错误消息
     */
    public UnauthorizedException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * 构造未授权异常（带原因）
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
