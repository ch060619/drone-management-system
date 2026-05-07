package com.example.drone.repository;

import com.example.drone.domain.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogRepository {
    int insert(SysOperationLog log);
}
