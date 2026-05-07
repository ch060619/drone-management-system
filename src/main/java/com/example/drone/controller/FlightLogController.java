package com.example.drone.controller;

import com.example.drone.domain.dto.*;
import com.example.drone.service.FlightLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/flight-logs")
public class FlightLogController {

    private final FlightLogService flightLogService;

    public FlightLogController(FlightLogService flightLogService) {
        this.flightLogService = flightLogService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('flightlog:create')")
    public ResponseEntity<ApiResponse<FlightLogDTO>> create(@Valid @RequestBody CreateFlightLogRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(flightLogService.createFlightLog(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('flightlog:list')")
    public ResponseEntity<ApiResponse<FlightLogDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(flightLogService.getFlightLogById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('flightlog:list')")
    public ResponseEntity<ApiResponse<Page<FlightLogDTO>>> list(FlightLogQueryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(flightLogService.listFlightLogs(request)));
    }

    @GetMapping("/drone/{droneId}")
    @PreAuthorize("hasAuthority('flightlog:list')")
    public ResponseEntity<ApiResponse<java.util.List<FlightLogDTO>>> getByDrone(@PathVariable Long droneId) {
        return ResponseEntity.ok(ApiResponse.success(flightLogService.getLogsByDroneId(droneId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('flightlog:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        flightLogService.deleteFlightLog(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}
