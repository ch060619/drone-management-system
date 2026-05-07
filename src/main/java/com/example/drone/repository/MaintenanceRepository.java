package com.example.drone.repository;

import com.example.drone.domain.entity.MaintenanceRecord;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MaintenanceRepository {
    int insert(MaintenanceRecord record);
    MaintenanceRecord selectById(Long id);
    List<MaintenanceRecord> selectByCondition(MaintenanceRecord condition);
    int countByCondition(MaintenanceRecord condition);
    int updateById(MaintenanceRecord record);
    int deleteById(Long id);
    int countPending();
    List<MaintenanceRecord> selectPending();
}
