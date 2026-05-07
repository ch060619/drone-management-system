package com.example.drone.repository;

import com.example.drone.domain.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogRepository {
    int insert(SysLoginLog log);
}
