package com.example.drone.repository;

import com.example.drone.domain.dto.DroneQueryConditions;
import com.example.drone.domain.entity.Drone;
import com.example.drone.domain.enums.DroneStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;

@Disabled("Integration test requires full context without Security/Redis/Mail auto-config — run with mvn test -Pintegration")
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class DroneRepositoryTest {

    @Autowired
    private DroneRepository droneRepository;

    private Drone testDrone;

    @BeforeEach
    void setUp() {
        testDrone = Drone.builder()
                .id(1L).serialNumber("TEST-001").modelName("Test Model")
                .manufacturer("Test Mfr").purchaseDate(LocalDate.of(2024, 1, 15))
                .status(DroneStatus.AVAILABLE).maxFlightTime(30)
                .maxFlightDistance(5000).weight(500).remarks("Test")
                .build();
    }

    @Test
    void testInsert_Success() {
        Drone drone = Drone.builder()
                .serialNumber("SN-TEST-001").modelName("Model").manufacturer("Mfr")
                .purchaseDate(LocalDate.now()).status(DroneStatus.AVAILABLE)
                .maxFlightTime(30).maxFlightDistance(5000).weight(500)
                .build();
        int rows = droneRepository.insert(drone);
        assertThat(rows).isEqualTo(1);
        assertThat(drone.getId()).isNotNull();
    }

    @Test
    void testSelectById_Success() {
        droneRepository.insert(buildDrone("SN-002"));
        Drone result = droneRepository.selectById(testDrone.getId());
        assertThat(result).isNotNull();
        assertThat(result.getSerialNumber()).isEqualTo("SN-002");
    }

    @Test
    void testSelectById_NotFound() {
        Drone result = droneRepository.selectById(999L);
        assertThat(result).isNull();
    }

    @Test
    void testSelectBySerialNumber_Success() {
        droneRepository.insert(buildDrone("SN-003"));
        Drone result = droneRepository.selectBySerialNumber("SN-003");
        assertThat(result).isNotNull();
    }

    @Test
    void testSelectBySerialNumber_NotFound() {
        Drone result = droneRepository.selectBySerialNumber("NONEXISTENT");
        assertThat(result).isNull();
    }

    @Test
    void testSelectByConditions_WithModelName() {
        droneRepository.insert(buildDrone("SN-004"));
        DroneQueryConditions c = new DroneQueryConditions();
        c.setModelName("Test Model");
        List<Drone> list = droneRepository.selectByConditions(c);
        assertThat(list).isNotEmpty();
    }

    @Test
    void testSelectByConditions_WithManufacturer() {
        droneRepository.insert(buildDrone("SN-005"));
        DroneQueryConditions c = new DroneQueryConditions();
        c.setManufacturer("Test Mfr");
        List<Drone> list = droneRepository.selectByConditions(c);
        assertThat(list).isNotEmpty();
    }

    @Test
    void testSelectByConditions_WithStatus() {
        droneRepository.insert(buildDrone("SN-006"));
        DroneQueryConditions c = new DroneQueryConditions();
        c.setStatus("AVAILABLE");
        List<Drone> list = droneRepository.selectByConditions(c);
        assertThat(list).isNotEmpty();
    }

    @Test
    void testSelectByConditions_WithPagination() {
        droneRepository.insert(buildDrone("SN-007"));
        DroneQueryConditions c = new DroneQueryConditions();
        c.setOffset(0);
        c.setLimit(10);
        List<Drone> list = droneRepository.selectByConditions(c);
        assertThat(list).isNotEmpty();
    }

    @Test
    void testSelectByConditions_NoMatch() {
        DroneQueryConditions c = new DroneQueryConditions();
        c.setModelName("NoSuchModel");
        List<Drone> list = droneRepository.selectByConditions(c);
        assertThat(list).isEmpty();
    }

    @Test
    void testCountByConditions_NoFilters() {
        droneRepository.insert(buildDrone("SN-008"));
        DroneQueryConditions c = new DroneQueryConditions();
        int count = droneRepository.countByConditions(c);
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testCountByConditions_WithFilters() {
        droneRepository.insert(buildDrone("SN-009"));
        DroneQueryConditions c = new DroneQueryConditions();
        c.setModelName("Test Model");
        int count = droneRepository.countByConditions(c);
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testUpdateById_Success() {
        droneRepository.insert(buildDrone("SN-010"));
        Drone update = Drone.builder().id(testDrone.getId()).remarks("Updated").build();
        int rows = droneRepository.updateById(update);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void testUpdateById_NotFound() {
        Drone update = Drone.builder().id(999L).remarks("Updated").build();
        int rows = droneRepository.updateById(update);
        assertThat(rows).isEqualTo(0);
    }

    @Test
    void testUpdateById_PartialUpdate() {
        droneRepository.insert(buildDrone("SN-011"));
        Drone update = Drone.builder().id(testDrone.getId()).remarks("Partial").build();
        int rows = droneRepository.updateById(update);
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void testDeleteById_Success() {
        droneRepository.insert(buildDrone("SN-012"));
        int rows = droneRepository.deleteById(testDrone.getId());
        assertThat(rows).isEqualTo(1);
    }

    @Test
    void testDeleteById_NotFound() {
        int rows = droneRepository.deleteById(999L);
        assertThat(rows).isEqualTo(0);
    }

    private Drone buildDrone(String serialNumber) {
        Drone d = Drone.builder()
                .serialNumber(serialNumber).modelName("Test Model").manufacturer("Test Mfr")
                .purchaseDate(LocalDate.of(2024, 1, 15)).status(DroneStatus.AVAILABLE)
                .maxFlightTime(30).maxFlightDistance(5000).weight(500)
                .build();
        droneRepository.insert(d);
        testDrone.setId(d.getId());
        return d;
    }
}
