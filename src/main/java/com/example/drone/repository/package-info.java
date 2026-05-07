/**
 * Repository 层 - 数据访问层
 * 
 * <p>职责：
 * <ul>
 *   <li>定义数据访问接口（MyBatis Mapper）</li>
 *   <li>执行数据库 CRUD 操作</li>
 *   <li>数据库适配器实现（adapter 子包）</li>
 * </ul>
 * 
 * <p>约束：
 * <ul>
 *   <li>接口使用 @Mapper 注解</li>
 *   <li>不得包含业务逻辑</li>
 *   <li>SQL 语句写在 XML 文件中</li>
 *   <li>使用参数化查询防止 SQL 注入</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.repository;
