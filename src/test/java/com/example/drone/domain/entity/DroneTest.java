package com.example.drone.domain.entity;

import com.example.drone.domain.enums.DroneStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Drone 实体类单元测试.
 *
 * @author 开发团队
 * @since 1.0.0
 */
class DroneTest {

    @Test
    void testBuilder() {
        LocalDate purchaseDate = LocalDate.of(2024, 1, 15);
        LocalDateTime now = LocalDateTime.now();

        Drone drone = Drone.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .purchaseDate(purchaseDate)
                .status(DroneStatus.AVAILABLE)
                .maxFlightTime(46)
                .maxFlightDistance(30000)
                .weight(895)
                .remarks("企业版")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, drone.getId());
        assertEquals("DJI-2024-001", drone.getSerialNumber());
        assertEquals("DJI Mavic 3", drone.getModelName());
        assertEquals("DJI", drone.getManufacturer());
        assertEquals(purchaseDate, drone.getPurchaseDate());
        assertEquals(DroneStatus.AVAILABLE, drone.getStatus());
        assertEquals(46, drone.getMaxFlightTime());
        assertEquals(30000, drone.getMaxFlightDistance());
        assertEquals(895, drone.getWeight());
        assertEquals("企业版", drone.getRemarks());
        assertEquals(now, drone.getCreatedAt());
        assertEquals(now, drone.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        Drone drone = new Drone();
        assertNull(drone.getId());
        assertNull(drone.getSerialNumber());
        assertNull(drone.getModelName());
        assertNull(drone.getManufacturer());
        assertNull(drone.getPurchaseDate());
        assertNull(drone.getStatus());
        assertNull(drone.getMaxFlightTime());
        assertNull(drone.getMaxFlightDistance());
        assertNull(drone.getWeight());
        assertNull(drone.getRemarks());
        assertNull(drone.getCreatedAt());
        assertNull(drone.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate purchaseDate = LocalDate.of(2024, 1, 15);
        LocalDateTime now = LocalDateTime.now();

        Drone drone = new Drone(
                1L,
                "DJI-2024-001",
                "DJI Mavic 3",
                "DJI",
                purchaseDate,
                DroneStatus.AVAILABLE,
                46,
                30000,
                895,
                "企业版",
                now,
                now
        );

        assertEquals(1L, drone.getId());
        assertEquals("DJI-2024-001", drone.getSerialNumber());
        assertEquals("DJI Mavic 3", drone.getModelName());
    }

    @Test
    void testSettersAndGetters() {
        Drone drone = new Drone();
        LocalDate purchaseDate = LocalDate.of(2024, 1, 15);
        LocalDateTime now = LocalDateTime.now();

        drone.setId(1L);
        drone.setSerialNumber("DJI-2024-001");
        drone.setModelName("DJI Mavic 3");
        drone.setManufacturer("DJI");
        drone.setPurchaseDate(purchaseDate);
        drone.setStatus(DroneStatus.AVAILABLE);
        drone.setMaxFlightTime(46);
        drone.setMaxFlightDistance(30000);
        drone.setWeight(895);
        drone.setRemarks("企业版");
        drone.setCreatedAt(now);
        drone.setUpdatedAt(now);

        assertEquals(1L, drone.getId());
        assertEquals("DJI-2024-001", drone.getSerialNumber());
        assertEquals("DJI Mavic 3", drone.getModelName());
        assertEquals("DJI", drone.getManufacturer());
        assertEquals(purchaseDate, drone.getPurchaseDate());
        assertEquals(DroneStatus.AVAILABLE, drone.getStatus());
        assertEquals(46, drone.getMaxFlightTime());
        assertEquals(30000, drone.getMaxFlightDistance());
        assertEquals(895, drone.getWeight());
        assertEquals("企业版", drone.getRemarks());
        assertEquals(now, drone.getCreatedAt());
        assertEquals(now, drone.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDate purchaseDate = LocalDate.of(2024, 1, 15);
        LocalDateTime now = LocalDateTime.now();

        Drone drone1 = Drone.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .purchaseDate(purchaseDate)
                .status(DroneStatus.AVAILABLE)
                .maxFlightTime(46)
                .maxFlightDistance(30000)
                .weight(895)
                .remarks("企业版")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Drone drone2 = Drone.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .manufacturer("DJI")
                .purchaseDate(purchaseDate)
                .status(DroneStatus.AVAILABLE)
                .maxFlightTime(46)
                .maxFlightDistance(30000)
                .weight(895)
                .remarks("企业版")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(drone1, drone2);
        assertEquals(drone1.hashCode(), drone2.hashCode());
    }

    @Test
    void testToString() {
        Drone drone = Drone.builder()
                .id(1L)
                .serialNumber("DJI-2024-001")
                .modelName("DJI Mavic 3")
                .build();

        String toString = drone.toString();
        assertNotNull(toString);
        // Lombok @Data 生成的 toString 包含类名和字段
        assert toString.contains("Drone");
        assert toString.contains("id=1");
        assert toString.contains("serialNumber=DJI-2024-001");
        assert toString.contains("modelName=DJI Mavic 3");
    }
}
