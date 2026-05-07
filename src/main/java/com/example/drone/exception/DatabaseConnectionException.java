package com.example.drone.exception;

/**
 * 数据库连接异常
 * 
 * <p>当数据库连接失败或数据库操作发生严重错误时抛出此异常。
 * 对应 HTTP 状态码 500 Internal Server Error。
 * 
 * <p>使用场景：
 * <ul>
 *   <li>数据库连接池无法获取连接</li>
 *   <li>数据库服务不可用</li>
 *   <li>数据库操作超时</li>
 *   <li>数据库配置错误导致连接失败</li>
 * </ul>
 * 
 * <p>注意：
 * <ul>
 *   <li>此异常表示系统级错误，需要运维人员介入</li>
 *   <li>应记录详细的错误日志，包括数据库类型、连接参数等</li>
 *   <li>不要在异常消息中暴露敏感信息（如数据库密码）</li>
 * </ul>
 * 
 * <p>示例：
 * <pre>
 * try {
 *     dataSource.getConnection();
 * } catch (SQLException e) {
 *     throw new DatabaseConnectionException("数据库连接失败", e);
 * }
 * </pre>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
public class DatabaseConnectionException extends DroneBusinessException {

    /**
     * HTTP 状态码 500
     */
    private static final int ERROR_CODE = 500;

    /**
     * 构造数据库连接异常
     * 
     * @param message 错误消息
     */
    public DatabaseConnectionException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * 构造数据库连接异常（带原因）
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
