package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 无人机响应 DTO.
 * <p>
 * 用于 API 响应，将实体对象转换为前端友好的格式。
 * 日期字段格式化为字符串，状态显示中文描述。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroneDTO {

    /**
     * 无人机 ID.
     */
    private Long id;

    /**
     * 序列号.
     */
    private String serialNumber;

    /**
     * 型号名称.
     */
    private String modelName;

    /**
     * 制造商.
     */
    private String manufacturer;

    /**
     * 购买日期（格式：yyyy-MM-dd）.
     */
    private String purchaseDate;

    /**
     * 状态描述（中文）.
     */
    private String status;

    /**
     * 最大飞行时间（分钟）.
     */
    private Integer maxFlightTime;

    /**
     * 最大飞行距离（米）.
     */
    private Integer maxFlightDistance;

    /**
     * 重量（克）.
     */
    private Integer weight;

    /**
     * 备注.
     */
    private String remarks;

    /**
     * 创建时间（ISO 8601 格式）.
     */
    private String createdAt;

    /**
     * 更新时间（ISO 8601 格式）.
     */
    private String updatedAt;
}
