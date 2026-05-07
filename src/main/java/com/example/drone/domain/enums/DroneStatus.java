package com.example.drone.domain.enums;

/**
 * 无人机状态枚举.
 * <p>
 * 定义无人机的三种状态：可用、维修中、已报废。
 * 每个枚举值包含对应的中文描述，用于前端展示和日志记录。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
public enum DroneStatus {

    /**
     * 可用状态 - 无人机处于正常工作状态，可以执行飞行任务.
     */
    AVAILABLE("可用"),

    /**
     * 维修中状态 - 无人机正在进行维护或修理，暂时不可用.
     */
    UNDER_MAINTENANCE("维修中"),

    /**
     * 已报废状态 - 无人机已达到使用寿命或损坏严重，不再使用.
     */
    SCRAPPED("已报废");

    /**
     * 状态的中文描述.
     */
    private final String description;

    /**
     * 构造函数.
     *
     * @param description 状态的中文描述
     */
    DroneStatus(String description) {
        this.description = description;
    }

    /**
     * 获取状态的中文描述.
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据中文描述获取对应的枚举值.
     *
     * @param description 状态描述
     * @return 对应的枚举值，如果未找到则返回 null
     */
    public static DroneStatus fromDescription(String description) {
        for (DroneStatus status : values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        return null;
    }
}
