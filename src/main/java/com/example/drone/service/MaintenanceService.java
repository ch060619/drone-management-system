package com.example.drone.service;

import com.example.drone.domain.dto.*;
import java.util.List;

public interface MaintenanceService {
    MaintenanceDTO createMaintenance(CreateMaintenanceRequest request);
    MaintenanceDTO getMaintenanceById(Long id);
    Page<MaintenanceDTO> listMaintenance(MaintenanceQueryRequest request);
    MaintenanceDTO updateMaintenance(Long id, MaintenanceDTO request);
    void deleteMaintenance(Long id);
    MaintenanceDTO completeMaintenance(Long id, String technician, String partsReplaced, Integer cost, String description);
    List<MaintenanceDTO> getPendingMaintenance();
}
