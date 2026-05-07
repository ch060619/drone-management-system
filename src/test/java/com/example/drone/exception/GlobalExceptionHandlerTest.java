package com.example.drone.exception;

import com.example.drone.domain.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * GlobalExceptionHandler 单元测试.
 * <p>
 * 测试全局异常处理器的各种异常处理场景。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    /**
     * 测试处理无人机不存在异常.
     * <p>
     * 验证返回 404 状态码和正确的错误消息。
     * </p>
     */
    @Test
    void testHandleDroneNotFound() {
        // Given
        DroneNotFoundException exception = new DroneNotFoundException("无人机不存在，ID: 1");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDroneNotFound(exception);

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals(404, response.getBody().getCode());
        assertEquals("无人机不存在，ID: 1", response.getBody().getMessage());
    }

    /**
     * 测试处理序列号重复异常.
     * <p>
     * 验证返回 400 状态码和正确的错误消息。
     * </p>
     */
    @Test
    void testHandleDuplicateSerialNumber() {
        // Given
        DuplicateSerialNumberException exception = new DuplicateSerialNumberException("序列号已存在: DJI-001");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateSerialNumber(exception);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(400, response.getBody().getCode());
        assertEquals("序列号已存在: DJI-001", response.getBody().getMessage());
    }

    /**
     * 测试处理参数校验失败异常.
     * <p>
     * 验证返回 400 状态码和字段错误详情。
     * </p>
     */
    @Test
    void testHandleValidationException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("createDroneRequest", "serialNumber", "序列号不能为空");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(400, response.getBody().getCode());
        assertEquals("参数校验失败", response.getBody().getMessage());
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().getData();
        assertNotNull(errors);
        assertEquals("序列号不能为空", errors.get("serialNumber"));
    }

    /**
     * 测试处理未授权异常.
     * <p>
     * 验证返回 401 状态码和正确的错误消息。
     * </p>
     */
    @Test
    void testHandleUnauthorized() {
        // Given
        UnauthorizedException exception = new UnauthorizedException("用户未登录");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorized(exception);

        // Then
        assertNotNull(response);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals(401, response.getBody().getCode());
        assertEquals("用户未登录", response.getBody().getMessage());
    }

    /**
     * 测试处理禁止访问异常.
     * <p>
     * 验证返回 403 状态码和正确的错误消息。
     * </p>
     */
    @Test
    void testHandleForbidden() {
        // Given
        ForbiddenException exception = new ForbiddenException("权限不足，需要管理员角色");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleForbidden(exception);

        // Then
        assertNotNull(response);
        assertEquals(403, response.getStatusCodeValue());
        assertEquals(403, response.getBody().getCode());
        assertEquals("权限不足，需要管理员角色", response.getBody().getMessage());
    }

    /**
     * 测试处理通用异常.
     * <p>
     * 验证返回 500 状态码和通用错误消息。
     * </p>
     */
    @Test
    void testHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("未知错误");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals(500, response.getBody().getCode());
        assertEquals("未知错误", response.getBody().getMessage());
    }

    /**
     * 测试处理参数校验失败异常（多个字段错误）.
     * <p>
     * 验证能够正确处理多个字段的验证错误。
     * </p>
     */
    @Test
    void testHandleValidationException_MultipleFields() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("createDroneRequest", "serialNumber", "序列号不能为空");
        FieldError fieldError2 = new FieldError("createDroneRequest", "modelName", "型号名称不能为空");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().getData();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertTrue(errors.containsKey("serialNumber"));
        assertTrue(errors.containsKey("modelName"));
    }
}
