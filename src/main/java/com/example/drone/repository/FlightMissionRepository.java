package com.example.drone.repository;

import com.example.drone.domain.entity.FlightMission;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FlightMissionRepository {
    int insert(FlightMission mission);
    FlightMission selectById(Long id);
    FlightMission selectByMissionCode(String missionCode);
    List<FlightMission> selectByCondition(FlightMission condition);
    int countByCondition(FlightMission condition);
    int updateById(FlightMission mission);
    int deleteById(Long id);
}
