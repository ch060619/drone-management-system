package com.example.drone.exception;

/**
 * 参数非法异常
 * 
 * <p>当业务逻辑层检测到参数不符合业务规则时抛出此异常。
 * 对应 HTTP 状态码 400 Bad Request。
 * 
 * <p>使用场景：
 * <ul>
 *   <li>参数值不符合业务规则（如日期格式错误、数值超出范围）</li>
 *   <li>参数组合不合法（如状态转换规则违反）</li>
 *   <li>业务逻辑校验失败（如无法将已报废的无人机改为可用状态）</li>
 * </ul>
 * 
 * <p>注意：
 * <ul>
 *   <li>此异常用于业务逻辑层的参数校验</li>
 *   <li>Controller 层的参数校验使用 Hibernate Validation 注解</li>
 *   <li>不要用于简单的 null 检查，应使用更具体的异常</li>
 * </ul>
 * 
 * <p>示例：
 * <pre>
 * if (purchaseDate.isAfter(LocalDate.now())) {
 *     throw new InvalidParameterException("购买日期不能晚于当前日期");
 * }
 * </pre>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
public class InvalidParameterException extends DroneBusinessException {

    /**
     * HTTP 状态码 400
     */
    private static final int ERROR_CODE = 400;

    /**
     * 构造参数非法异常
     * 
     * @param message 错误消息
     */
    public InvalidParameterException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * 构造参数非法异常（带原因）
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public InvalidParameterException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
