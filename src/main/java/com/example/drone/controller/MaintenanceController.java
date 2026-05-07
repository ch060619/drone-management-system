package com.example.drone.controller;

import com.example.drone.domain.dto.*;
import com.example.drone.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('maintenance:create')")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> create(@Valid @RequestBody CreateMaintenanceRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(maintenanceService.createMaintenance(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('maintenance:list')")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(maintenanceService.getMaintenanceById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('maintenance:list')")
    public ResponseEntity<ApiResponse<Page<MaintenanceDTO>>> list(MaintenanceQueryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(maintenanceService.listMaintenance(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('maintenance:update')")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> update(@PathVariable Long id, @RequestBody MaintenanceDTO request) {
        return ResponseEntity.ok(ApiResponse.success(maintenanceService.updateMaintenance(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('maintenance:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('maintenance:list')")
    public ResponseEntity<ApiResponse<java.util.List<MaintenanceDTO>>> pending() {
        return ResponseEntity.ok(ApiResponse.success(maintenanceService.getPendingMaintenance()));
    }
}
