package com.example.drone.domain.entity;

import com.example.drone.domain.enums.DroneStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 无人机实体类.
 * <p>
 * 对应数据库表 t_drone，包含无人机的所有基本信息和状态。
 * 使用 Lombok 注解简化 getter/setter/builder 等样板代码。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drone {

    /**
     * 主键 ID.
     * <p>
     * 数据库自动生成的唯一标识符。
     * </p>
     */
    private Long id;

    /**
     * 序列号.
     * <p>
     * 无人机制造商提供的唯一序列号，用于标识具体的无人机设备。
     * 该字段在数据库中具有唯一约束。
     * </p>
     */
    private String serialNumber;

    /**
     * 型号名称.
     * <p>
     * 无人机的型号，例如：DJI Mavic 3、Phantom 4 Pro。
     * </p>
     */
    private String modelName;

    /**
     * 制造商.
     * <p>
     * 无人机的制造商名称，例如：DJI、Parrot。
     * </p>
     */
    private String manufacturer;

    /**
     * 购买日期.
     * <p>
     * 无人机的采购日期，用于计算使用年限和折旧。
     * </p>
     */
    private LocalDate purchaseDate;

    /**
     * 状态.
     * <p>
     * 无人机当前的状态，包括：可用、维修中、已报废。
     * </p>
     */
    private DroneStatus status;

    /**
     * 最大飞行时间（分钟）.
     * <p>
     * 单次充电后的最大飞行时长，单位为分钟。
     * </p>
     */
    private Integer maxFlightTime;

    /**
     * 最大飞行距离（米）.
     * <p>
     * 无人机的最大飞行距离，单位为米。
     * </p>
     */
    private Integer maxFlightDistance;

    /**
     * 重量（克）.
     * <p>
     * 无人机的重量，单位为克。
     * </p>
     */
    private Integer weight;

    /**
     * 备注.
     * <p>
     * 其他补充信息，例如配置说明、特殊用途等。
     * 该字段为可选字段。
     * </p>
     */
    private String remarks;

    /**
     * 创建时间.
     * <p>
     * 记录在数据库中创建的时间戳，由数据库自动生成。
     * </p>
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间.
     * <p>
     * 记录最后一次更新的时间戳，由数据库自动维护。
     * </p>
     */
    private LocalDateTime updatedAt;
}
