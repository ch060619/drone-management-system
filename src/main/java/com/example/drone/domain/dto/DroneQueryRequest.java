package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 无人机查询请求 DTO.
 * <p>
 * 用于接收分页查询无人机列表的请求参数，支持按型号、制造商、状态进行过滤。
 * 所有字段均为可选。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneQueryRequest {

    /**
     * 型号名称（模糊查询）.
     * <p>
     * 可选，如果提供则进行模糊匹配。
     * </p>
     */
    private String modelName;

    /**
     * 制造商（模糊查询）.
     * <p>
     * 可选，如果提供则进行模糊匹配。
     * </p>
     */
    private String manufacturer;

    /**
     * 状态（精确匹配）.
     * <p>
     * 可选，有效值：AVAILABLE、UNDER_MAINTENANCE、SCRAPPED。
     * </p>
     */
    private String status;

    /**
     * 页码.
     * <p>
     * 可选，默认为 1。
     * </p>
     */
    private Integer pageNum;

    /**
     * 每页数量.
     * <p>
     * 可选，默认为 20。
     * </p>
     */
    private Integer pageSize;

    /**
     * 获取页码，如果未设置则返回默认值 1.
     *
     * @return 页码
     */
    public Integer getPageNum() {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    /**
     * 获取每页数量，如果未设置则返回默认值 20.
     *
     * @return 每页数量
     */
    public Integer getPageSize() {
        return pageSize == null || pageSize < 1 ? 20 : pageSize;
    }
}
