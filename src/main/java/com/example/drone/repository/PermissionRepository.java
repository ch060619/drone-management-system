package com.example.drone.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionRepository {
    List<String> selectPermCodesByUserId(Long userId);
}
