package com.example.drone.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请求拦截器，负责拦截所有 HTTP 请求并记录详细的请求和响应信息。
 *
 * <p>功能包括：
 * <ul>
 *   <li>记录请求方法、路径、参数、客户端 IP、User-Agent 和时间戳</li>
 *   <li>记录响应状态码和处理耗时</li>
 *   <li>对耗时超过 1000ms 的请求输出慢请求警告</li>
 *   <li>支持从 X-Forwarded-For 和 X-Real-IP 头获取真实客户端 IP</li>
 * </ul>
 *
 * <p>关联需求：需求 6.1, 6.2, 6.3, 6.4, 6.5, 6.6
 *
 * @author 开发团队
 * @since 1.0
 */
@Component
public class RequestInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestInterceptor.class);

    /**
     * 请求开始时间属性名，用于在请求属性中存储时间戳。
     */
    private static final String START_TIME_ATTRIBUTE = "startTime";

    /**
     * 慢请求阈值（毫秒），超过此阈值的请求将被记录为慢请求。
     */
    private static final long SLOW_REQUEST_THRESHOLD_MS = 1000L;

    /**
     * 在请求处理之前执行，记录请求的详细信息。
     *
     * <p>记录内容包括：
     * <ul>
     *   <li>HTTP 方法（GET、POST、PUT、DELETE 等）</li>
     *   <li>请求路径（URI）</li>
     *   <li>请求参数（查询字符串参数）</li>
     *   <li>客户端 IP 地址（支持代理场景）</li>
     *   <li>User-Agent 信息</li>
     *   <li>请求开始时间戳</li>
     * </ul>
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  处理器对象
     * @return 始终返回 true，允许请求继续处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        log.info("请求开始 - 方法: {}, 路径: {}, 参数: {}, IP: {}, User-Agent: {}",
                request.getMethod(),
                request.getRequestURI(),
                getRequestParams(request),
                getClientIp(request),
                request.getHeader("User-Agent"));

        return true;
    }

    /**
     * 在请求处理完成后执行，记录响应信息和处理耗时。
     *
     * <p>记录内容包括：
     * <ul>
     *   <li>HTTP 方法</li>
     *   <li>请求路径</li>
     *   <li>响应状态码</li>
     *   <li>处理耗时（毫秒）</li>
     * </ul>
     *
     * <p>如果处理耗时超过 {@link #SLOW_REQUEST_THRESHOLD_MS}，将输出慢请求警告日志。
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  处理器对象
     * @param ex       异常对象（如果有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime == null) {
            log.warn("请求开始时间未记录 - 路径: {}", request.getRequestURI());
            return;
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("请求结束 - 方法: {}, 路径: {}, 状态码: {}, 耗时: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);

        if (duration > SLOW_REQUEST_THRESHOLD_MS) {
            log.warn("慢请求警告 - 路径: {}, 耗时: {}ms", request.getRequestURI(), duration);
        }
    }

    /**
     * 获取请求参数字符串。
     *
     * <p>将请求的查询参数转换为 key=value 格式的字符串，多个参数用 & 连接。
     * 如果参数为空，返回空对象字符串 "{}"。
     *
     * @param request HTTP 请求对象
     * @return 请求参数字符串，格式为 "key1=value1&key2=value2"，如果无参数则返回 "{}"
     */
    private String getRequestParams(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        if (paramMap.isEmpty()) {
            return "{}";
        }
        return paramMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    /**
     * 获取客户端真实 IP 地址。
     *
     * <p>按以下优先级获取 IP 地址：
     * <ol>
     *   <li>X-Forwarded-For 请求头（代理场景，取第一个 IP）</li>
     *   <li>X-Real-IP 请求头（Nginx 代理场景）</li>
     *   <li>request.getRemoteAddr()（直连场景）</li>
     * </ol>
     *
     * <p>如果请求头值为 null、空字符串或 "unknown"，则尝试下一个来源。
     *
     * @param request HTTP 请求对象
     * @return 客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果 X-Forwarded-For 包含多个 IP（逗号分隔），取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
