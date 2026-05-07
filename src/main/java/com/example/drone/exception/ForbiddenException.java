package com.example.drone.exception;

/**
 * 禁止访问异常
 * 
 * <p>当用户已通过身份认证，但没有足够权限访问资源时抛出此异常。
 * 对应 HTTP 状态码 403 Forbidden。
 * 
 * <p>使用场景：
 * <ul>
 *   <li>普通用户尝试访问管理员专属功能</li>
 *   <li>用户尝试访问其他用户的私有资源</li>
 *   <li>角色权限不足</li>
 * </ul>
 * 
 * <p>示例：
 * <pre>
 * if (!currentUser.hasRole("admin")) {
 *     throw new ForbiddenException("权限不足，需要管理员角色");
 * }
 * </pre>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
public class ForbiddenException extends DroneBusinessException {

    /**
     * HTTP 状态码 403
     */
    private static final int ERROR_CODE = 403;

    /**
     * 构造禁止访问异常
     * 
     * @param message 错误消息
     */
    public ForbiddenException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * 构造禁止访问异常（带原因）
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ForbiddenException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
