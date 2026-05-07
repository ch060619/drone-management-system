package com.example.drone.config;

import com.example.drone.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类，负责注册拦截器和其他 Web 相关配置。
 *
 * <p>功能包括：
 * <ul>
 *   <li>注册 {@link RequestInterceptor} 拦截所有 HTTP 请求</li>
 *   <li>排除静态资源路径（/static/**）和错误页面（/error）</li>
 * </ul>
 *
 * <p>关联需求：需求 6.1
 *
 * @author 开发团队
 * @since 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 请求拦截器，由 Spring 容器自动注入。
     */
    private final RequestInterceptor requestInterceptor;

    /**
     * 构造器注入 RequestInterceptor。
     *
     * @param requestInterceptor 请求拦截器实例
     */
    @Autowired
    public WebMvcConfig(RequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    /**
     * 注册拦截器到 Spring MVC 拦截器链。
     *
     * <p>拦截规则：
     * <ul>
     *   <li>拦截所有路径：/**</li>
     *   <li>排除静态资源：/static/**</li>
     *   <li>排除错误页面：/error</li>
     * </ul>
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/error");
    }
}
