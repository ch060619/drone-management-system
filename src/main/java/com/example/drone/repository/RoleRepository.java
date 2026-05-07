package com.example.drone.repository;

import com.example.drone.domain.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleRepository {
    SysRole selectByCode(String roleCode);
    List<SysRole> selectByUserId(Long userId);
}
