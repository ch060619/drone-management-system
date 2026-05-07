package com.example.drone.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * DroneStatus 枚举单元测试.
 *
 * @author 开发团队
 * @since 1.0.0
 */
class DroneStatusTest {

    @Test
    void testGetDescription() {
        assertEquals("可用", DroneStatus.AVAILABLE.getDescription());
        assertEquals("维修中", DroneStatus.UNDER_MAINTENANCE.getDescription());
        assertEquals("已报废", DroneStatus.SCRAPPED.getDescription());
    }

    @Test
    void testFromDescription_ValidDescription() {
        assertEquals(DroneStatus.AVAILABLE, DroneStatus.fromDescription("可用"));
        assertEquals(DroneStatus.UNDER_MAINTENANCE, DroneStatus.fromDescription("维修中"));
        assertEquals(DroneStatus.SCRAPPED, DroneStatus.fromDescription("已报废"));
    }

    @Test
    void testFromDescription_InvalidDescription() {
        assertNull(DroneStatus.fromDescription("不存在的状态"));
        assertNull(DroneStatus.fromDescription(""));
        assertNull(DroneStatus.fromDescription(null));
    }

    @Test
    void testEnumValues() {
        DroneStatus[] values = DroneStatus.values();
        assertEquals(3, values.length);
        assertEquals(DroneStatus.AVAILABLE, values[0]);
        assertEquals(DroneStatus.UNDER_MAINTENANCE, values[1]);
        assertEquals(DroneStatus.SCRAPPED, values[2]);
    }

    @Test
    void testValueOf() {
        assertEquals(DroneStatus.AVAILABLE, DroneStatus.valueOf("AVAILABLE"));
        assertEquals(DroneStatus.UNDER_MAINTENANCE, DroneStatus.valueOf("UNDER_MAINTENANCE"));
        assertEquals(DroneStatus.SCRAPPED, DroneStatus.valueOf("SCRAPPED"));
    }
}
