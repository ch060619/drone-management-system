package com.example.drone.exception;

import com.example.drone.domain.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器.
 * <p>
 * 统一处理系统中的所有异常，将异常转换为标准的错误响应格式。
 * 使用 @RestControllerAdvice 注解，自动应用于所有 @RestController。
 * </p>
 * 
 * <p>处理的异常类型：
 * <ul>
 *   <li>DroneNotFoundException - 无人机不存在（404）</li>
 *   <li>DuplicateSerialNumberException - 序列号重复（400）</li>
 *   <li>MethodArgumentNotValidException - 参数校验失败（400）</li>
 *   <li>UnauthorizedException - 未授权（401）</li>
 *   <li>ForbiddenException - 禁止访问（403）</li>
 *   <li>Exception - 其他未处理异常（500）</li>
 * </ul>
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理无人机不存在异常.
     * <p>
     * 当查询、更新或删除不存在的无人机时触发。
     * 返回 HTTP 404 状态码。
     * </p>
     *
     * @param ex 无人机不存在异常
     * @return 错误响应（404）
     */
    @ExceptionHandler(DroneNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDroneNotFound(DroneNotFoundException ex) {
        log.warn("无人机不存在: {}", ex.getMessage());
        return ResponseEntity.status(404)
                .body(ErrorResponse.of(404, ex.getMessage()));
    }

    /**
     * 处理序列号重复异常.
     * <p>
     * 当创建或更新无人机时，序列号与已有记录冲突时触发。
     * 返回 HTTP 400 状态码。
     * </p>
     *
     * @param ex 序列号重复异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(DuplicateSerialNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSerialNumber(DuplicateSerialNumberException ex) {
        log.warn("序列号重复: {}", ex.getMessage());
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, ex.getMessage()));
    }

    /**
     * 处理参数校验失败异常.
     * <p>
     * 当请求参数不符合 @Valid 注解定义的校验规则时触发。
     * 返回 HTTP 400 状态码，并包含具体的字段验证错误信息。
     * </p>
     *
     * @param ex 参数校验异常
     * @return 错误响应（400），包含字段错误详情
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("参数校验失败: {}", errors);
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "参数校验失败", errors));
    }

    /**
     * 处理未授权异常.
     * <p>
     * 当用户未通过身份认证就访问需要认证的资源时触发。
     * 返回 HTTP 401 状态码。
     * </p>
     *
     * @param ex 未授权异常
     * @return 错误响应（401）
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        log.warn("未授权访问: {}", ex.getMessage());
        return ResponseEntity.status(401)
                .body(ErrorResponse.of(401, ex.getMessage()));
    }

    /**
     * 处理禁止访问异常.
     * <p>
     * 当用户已认证但权限不足时触发。
     * 返回 HTTP 403 状态码。
     * </p>
     *
     * @param ex 禁止访问异常
     * @return 错误响应（403）
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        log.warn("权限不足: {}", ex.getMessage());
        return ResponseEntity.status(403)
                .body(ErrorResponse.of(403, ex.getMessage()));
    }

    /**
     * 处理参数非法异常.
     * <p>
     * 当业务逻辑层检测到参数不合规时触发。
     * 返回 HTTP 400 状态码。
     * </p>
     *
     * @param ex 参数非法异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(InvalidParameterException ex) {
        log.warn("参数非法: {}", ex.getMessage());
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, ex.getMessage()));
    }

    /**
     * 处理业务异常基类.
     * <p>
     * 捕获所有 DroneBusinessException 子类（未被更具体的处理器捕获的）。
     * 使用异常自带的 errorCode 设置 HTTP 状态码。
     * </p>
     *
     * @param ex 业务异常
     * @return 错误响应（动态状态码）
     */
    @ExceptionHandler(DroneBusinessException.class)
    public ResponseEntity<ErrorResponse> handleDroneBusiness(DroneBusinessException ex) {
        log.error("业务异常: code={}, message={}", ex.getErrorCode(), ex.getMessage(), ex);
        return ResponseEntity.status(ex.getErrorCode())
                .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * 处理数据库连接异常.
     * <p>
     * 当数据库连接失败、超时或不可用时触发。
     * 返回 HTTP 503 状态码，提示服务暂时不可用。
     * </p>
     *
     * @param ex 数据库连接异常
     * @return 错误响应（503）
     */
    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseConnection(DatabaseConnectionException ex) {
        log.error("数据库连接异常: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of(503, "数据库服务不可用，请稍后重试"));
    }

    /**
     * 处理 HTTP 请求体不可读异常.
     * <p>
     * 当请求体 JSON 格式错误、字段类型不匹配时触发。
     * 返回 HTTP 400 状态码。
     * </p>
     *
     * @param ex 消息不可读异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求体无法解析: {}", ex.getMessage());
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "请求体格式错误，请检查 JSON 格式"));
    }

    /**
     * 处理方法参数类型不匹配异常.
     * <p>
     * 当路径变量或请求参数的类型与期望类型不匹配时触发（如 ID 应为数字却传入字符串）。
     * 返回 HTTP 400 状态码。
     * </p>
     *
     * @param ex 类型不匹配异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("参数类型不匹配: name={}, value={}, requiredType={}",
                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        String message = String.format("参数 '%s' 类型错误，期望类型: %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "未知");
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, message));
    }

    /**
     * 处理 HTTP 请求方法不支持异常.
     * <p>
     * 当使用错误的 HTTP 方法访问端点时触发（如用 GET 访问 POST 端点）。
     * 返回 HTTP 405 状态码。
     * </p>
     *
     * @param ex 方法不支持异常
     * @return 错误响应（405）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("不支持的请求方法: {}", ex.getMethod());
        return ResponseEntity.status(405)
                .body(ErrorResponse.of(405, "不支持的请求方法: " + ex.getMethod()
                        + "，支持的方法: " + ex.getSupportedHttpMethods()));
    }

    /**
     * 处理 HTTP 媒体类型不支持异常.
     * <p>
     * 当请求的 Content-Type 不被支持时触发。
     * 返回 HTTP 415 状态码。
     * </p>
     *
     * @param ex 媒体类型不支持异常
     * @return 错误响应（415）
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.warn("不支持的媒体类型: {}", ex.getContentType());
        return ResponseEntity.status(415)
                .body(ErrorResponse.of(415, "不支持的 Content-Type，请使用 application/json"));
    }

    /**
     * 处理缺失必要请求参数异常.
     * <p>
     * 当请求缺少必要的查询参数或表单参数时触发。
     * 返回 HTTP 400 状态码。
     * </p>
     *
     * @param ex 缺失参数异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.warn("缺少必要参数: name={}, type={}", ex.getParameterName(), ex.getParameterType());
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "缺少必要参数: " + ex.getParameterName()));
    }

    /**
     * 处理数据绑定异常.
     * <p>
     * 当请求参数绑定到 Java 对象失败时触发。
     * 返回 HTTP 400 状态码，包含字段级错误详情。
     * </p>
     *
     * @param ex 数据绑定异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError
                    ? ((FieldError) error).getField()
                    : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("数据绑定失败: {}", errors);
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "参数绑定失败", errors));
    }

    /**
     * 处理约束校验异常.
     * <p>
     * 当方法参数或返回值违反 Bean Validation 约束时触发。
     * 返回 HTTP 400 状态码。
     * </p>
     *
     * @param ex 约束校验异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String violations = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("约束校验失败: {}", violations);
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "参数校验失败: " + violations));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("权限不足: {}", ex.getMessage());
        return ResponseEntity.status(403)
                .body(ErrorResponse.of(403, "权限不足，无法访问该资源"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.warn("认证失败: {}", ex.getMessage());
        return ResponseEntity.status(401)
                .body(ErrorResponse.of(401, "未认证，请先登录"));
    }

    /**
     * 处理其他未捕获的异常.
     * <p>
     * 作为兜底处理器，捕获所有未被其他处理器处理的异常。
     * 返回 HTTP 500 状态码，并记录详细的错误日志。
     * </p>
     *
     * @param ex 未处理的异常
     * @return 错误响应（500）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String detail = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        log.error("系统内部错误: {}", detail, ex);
        return ResponseEntity.status(500)
                .body(ErrorResponse.of(500, detail));
    }
}
