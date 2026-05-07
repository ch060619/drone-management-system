package com.example.drone.service;

import com.example.drone.domain.dto.*;
import java.util.List;

public interface MissionService {
    MissionDTO createMission(CreateMissionRequest request);
    MissionDTO getMissionById(Long id);
    Page<MissionDTO> listMissions(MissionQueryRequest request);
    MissionDTO updateMission(Long id, MissionDTO request);
    void cancelMission(Long id);
    void permanentlyDeleteMission(Long id);
    void deleteMission(Long id);
    MissionDTO restoreMission(Long id);
    MissionDTO completeMission(Long id);
    List<MissionDTO> getMissionsByDroneId(Long droneId);
}
