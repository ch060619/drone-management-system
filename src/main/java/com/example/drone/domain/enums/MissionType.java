package com.example.drone.domain.enums;

public enum MissionType {
    INSPECTION("巡检"),
    AERIAL_PHOTOGRAPHY("航拍"),
    SURVEYING("测绘"),
    TRANSPORT("运输"),
    OTHER("其他");

    private final String description;

    MissionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
