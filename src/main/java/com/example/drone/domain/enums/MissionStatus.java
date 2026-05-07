package com.example.drone.domain.enums;

public enum MissionStatus {
    PENDING("待执行"),
    IN_PROGRESS("执行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    MissionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
