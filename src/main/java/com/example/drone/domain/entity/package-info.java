/**
 * 领域实体包.
 * <p>
 * 包含系统的核心领域实体类，这些实体类直接映射到数据库表。
 * 实体类使用 Lombok 注解简化代码，包含完整的 Javadoc 注释。
 * </p>
 * <p>
 * 实体类设计原则：
 * <ul>
 *   <li>实体类仅包含数据字段和基本的 getter/setter 方法</li>
 *   <li>不包含业务逻辑，业务逻辑应放在 Service 层</li>
 *   <li>使用 Lombok 的 @Data、@Builder、@NoArgsConstructor、@AllArgsConstructor 注解</li>
 *   <li>字段命名使用驼峰命名法，与数据库字段通过 MyBatis 自动映射</li>
 * </ul>
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
package com.example.drone.domain.entity;
