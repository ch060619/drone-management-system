/**
 * Security 层 - 安全认证和授权
 * 
 * <p>职责：
 * <ul>
 *   <li>JWT Token 生成与验证（JwtTokenProvider）</li>
 *   <li>请求拦截与 Token 解析（JwtAuthenticationFilter）</li>
 *   <li>用户身份认证（Authentication）</li>
 *   <li>用户权限授权（Authorization）</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>Token 签名使用 HMAC-SHA256</li>
 *   <li>Access Token 有效期 2 小时，Refresh Token 有效期 7/30 天</li>
 *   <li>生产环境 JWT 密钥通过环境变量注入</li>
 *   <li>避免在日志中输出敏感信息（密码、Token）</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 2.0.0
 * @since 2024-01-15
 */
package com.example.drone.security;
