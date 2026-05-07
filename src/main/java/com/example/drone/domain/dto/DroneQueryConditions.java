package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 无人机查询条件类.
 * <p>
 * 用于封装无人机列表查询的条件参数，支持按型号、制造商、状态进行筛选，
 * 并支持分页查询。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneQueryConditions {

    /**
     * 型号名称（模糊查询）.
     * <p>
     * 如果指定，将查询型号名称包含该关键字的无人机。
     * </p>
     */
    private String modelName;

    /**
     * 制造商（模糊查询）.
     * <p>
     * 如果指定，将查询制造商名称包含该关键字的无人机。
     * </p>
     */
    private String manufacturer;

    /**
     * 状态（精确匹配）.
     * <p>
     * 如果指定，将查询指定状态的无人机（AVAILABLE、UNDER_MAINTENANCE、SCRAPPED）。
     * </p>
     */
    private String status;

    /**
     * 分页起始位置（OFFSET）.
     * <p>
     * 用于分页查询，表示从第几条记录开始返回。
     * 计算公式：offset = (pageNum - 1) * pageSize
     * </p>
     */
    private Integer offset;

    /**
     * 每页记录数（LIMIT）.
     * <p>
     * 用于分页查询，表示每页返回的记录数量。
     * </p>
     */
    private Integer limit;
}
