package com.example.drone.controller;

import com.example.drone.domain.dto.*;
import com.example.drone.service.MissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/missions")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('mission:create')")
    public ResponseEntity<ApiResponse<MissionDTO>> create(@Valid @RequestBody CreateMissionRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(missionService.createMission(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('mission:list')")
    public ResponseEntity<ApiResponse<MissionDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(missionService.getMissionById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('mission:list')")
    public ResponseEntity<ApiResponse<Page<MissionDTO>>> list(MissionQueryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(missionService.listMissions(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('mission:update')")
    public ResponseEntity<ApiResponse<MissionDTO>> update(@PathVariable Long id, @RequestBody MissionDTO request) {
        return ResponseEntity.ok(ApiResponse.success(missionService.updateMission(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('mission:delete')")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        missionService.cancelMission(id);
        return ResponseEntity.ok(ApiResponse.success("已取消", null));
    }

    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasAuthority('mission:delete')")
    public ResponseEntity<ApiResponse<Void>> permanentDelete(@PathVariable Long id) {
        missionService.permanentlyDeleteMission(id);
        return ResponseEntity.ok(ApiResponse.success("已彻底删除", null));
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('mission:update')")
    public ResponseEntity<ApiResponse<MissionDTO>> restore(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(missionService.restoreMission(id)));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('mission:update')")
    public ResponseEntity<ApiResponse<MissionDTO>> complete(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(missionService.completeMission(id)));
    }
}
