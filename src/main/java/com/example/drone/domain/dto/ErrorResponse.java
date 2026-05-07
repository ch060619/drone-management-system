package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误响应格式.
 * <p>
 * 用于封装所有 API 的错误响应，包含错误码、错误消息和详细错误信息。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 错误状态码.
     * <p>
     * 通常与 HTTP 状态码一致，例如：400（参数错误）、404（未找到）、500（服务器错误）。
     * </p>
     */
    private int code;

    /**
     * 错误消息.
     * <p>
     * 简短的错误描述，例如："参数校验失败"、"无人机不存在"。
     * </p>
     */
    private String message;

    /**
     * 详细错误信息.
     * <p>
     * 可选字段，用于提供更详细的错误信息。
     * 例如：字段验证错误时，包含具体的字段名称和错误原因。
     * </p>
     */
    private Object data;

    /**
     * 创建错误响应（不包含详细信息）.
     *
     * @param code    错误状态码
     * @param message 错误消息
     * @return 错误响应对象
     */
    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message, null);
    }

    /**
     * 创建错误响应（包含详细信息）.
     *
     * @param code    错误状态码
     * @param message 错误消息
     * @param data    详细错误信息
     * @return 错误响应对象
     */
    public static ErrorResponse of(int code, String message, Object data) {
        return new ErrorResponse(code, message, data);
    }
}
