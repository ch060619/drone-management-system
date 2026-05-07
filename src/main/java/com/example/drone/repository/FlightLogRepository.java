package com.example.drone.repository;

import com.example.drone.domain.entity.FlightLog;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FlightLogRepository {
    int insert(FlightLog log);
    FlightLog selectById(Long id);
    List<FlightLog> selectByDroneId(Long droneId);
    int countByDroneId(Long droneId);
    List<FlightLog> selectAll();
    int deleteById(Long id);
}
