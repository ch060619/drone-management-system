package com.example.drone.domain.enums;

public enum MaintenanceType {
    ROUTINE("定期维护"),
    REPAIR("维修"),
    INSPECTION("检查"),
    PART_REPLACEMENT("更换部件");

    private final String description;

    MaintenanceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
