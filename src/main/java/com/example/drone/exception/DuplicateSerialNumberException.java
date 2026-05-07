package com.example.drone.exception;

/**
 * 序列号重复异常
 * 
 * <p>当创建或更新无人机时，序列号与数据库中已有记录重复时抛出此异常。
 * 对应 HTTP 状态码 400 Bad Request。
 * 
 * <p>使用场景：
 * <ul>
 *   <li>创建新无人机时，序列号已存在于数据库中</li>
 *   <li>更新无人机序列号时，新序列号与其他记录冲突</li>
 * </ul>
 * 
 * <p>示例：
 * <pre>
 * Drone existing = droneRepository.selectBySerialNumber(serialNumber);
 * if (existing != null) {
 *     throw new DuplicateSerialNumberException("序列号已存在: " + serialNumber);
 * }
 * </pre>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
public class DuplicateSerialNumberException extends DroneBusinessException {

    /**
     * HTTP 状态码 400
     */
    private static final int ERROR_CODE = 400;

    /**
     * 构造序列号重复异常
     * 
     * @param message 错误消息
     */
    public DuplicateSerialNumberException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * 构造序列号重复异常（带原因）
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DuplicateSerialNumberException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
