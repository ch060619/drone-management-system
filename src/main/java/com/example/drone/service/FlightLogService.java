package com.example.drone.service;

import com.example.drone.domain.dto.*;
import java.util.List;

public interface FlightLogService {
    FlightLogDTO createFlightLog(CreateFlightLogRequest request);
    FlightLogDTO getFlightLogById(Long id);
    Page<FlightLogDTO> listFlightLogs(FlightLogQueryRequest request);
    void deleteFlightLog(Long id);
    List<FlightLogDTO> getLogsByDroneId(Long droneId);
}
