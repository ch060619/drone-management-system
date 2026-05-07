package com.example.drone.exception;

/**
 * 无人机不存在异常
 * 
 * <p>当查询、更新或删除不存在的无人机时抛出此异常。
 * 对应 HTTP 状态码 404 Not Found。
 * 
 * <p>使用场景：
 * <ul>
 *   <li>根据 ID 查询无人机时，数据库中不存在该记录</li>
 *   <li>更新无人机信息时，目标无人机不存在</li>
 *   <li>删除无人机时，目标无人机不存在</li>
 * </ul>
 * 
 * <p>示例：
 * <pre>
 * if (drone == null) {
 *     throw new DroneNotFoundException("无人机不存在，ID: " + id);
 * }
 * </pre>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
public class DroneNotFoundException extends DroneBusinessException {

    /**
     * HTTP 状态码 404
     */
    private static final int ERROR_CODE = 404;

    /**
     * 构造无人机不存在异常
     * 
     * @param message 错误消息
     */
    public DroneNotFoundException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * 构造无人机不存在异常（带原因）
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DroneNotFoundException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
