package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 更新无人机请求 DTO.
 * <p>
 * 用于接收更新无人机的请求参数，支持部分更新。
 * 所有字段均为可选，只更新提供的字段。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDroneRequest {

    /**
     * 序列号.
     * <p>
     * 可选，如果提供则最大长度 100 个字符，必须唯一。
     * </p>
     */
    @Size(max = 100, message = "序列号长度不能超过 100 个字符")
    private String serialNumber;

    /**
     * 型号名称.
     * <p>
     * 可选，如果提供则最大长度 100 个字符。
     * </p>
     */
    @Size(max = 100, message = "型号名称长度不能超过 100 个字符")
    private String modelName;

    /**
     * 制造商.
     * <p>
     * 可选，如果提供则最大长度 100 个字符。
     * </p>
     */
    @Size(max = 100, message = "制造商长度不能超过 100 个字符")
    private String manufacturer;

    /**
     * 购买日期.
     * <p>
     * 可选，如果提供则格式必须为 yyyy-MM-dd。
     * </p>
     */
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "购买日期格式必须为 yyyy-MM-dd")
    private String purchaseDate;

    /**
     * 状态.
     * <p>
     * 可选，如果提供则有效值：AVAILABLE、UNDER_MAINTENANCE、SCRAPPED。
     * </p>
     */
    private String status;

    /**
     * 最大飞行时间（分钟）.
     * <p>
     * 可选，如果提供则必须大于 0。
     * </p>
     */
    @Min(value = 1, message = "最大飞行时间必须大于 0")
    private Integer maxFlightTime;

    /**
     * 最大飞行距离（米）.
     * <p>
     * 可选，如果提供则必须大于 0。
     * </p>
     */
    @Min(value = 1, message = "最大飞行距离必须大于 0")
    private Integer maxFlightDistance;

    /**
     * 重量（克）.
     * <p>
     * 可选，如果提供则必须大于 0。
     * </p>
     */
    @Min(value = 1, message = "重量必须大于 0")
    private Integer weight;

    /**
     * 备注.
     * <p>
     * 可选，如果提供则最大长度 500 个字符。
     * </p>
     */
    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remarks;
}
