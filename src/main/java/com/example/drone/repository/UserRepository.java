package com.example.drone.repository;

import com.example.drone.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRepository {
    int insert(SysUser user);
    SysUser selectById(Long id);
    SysUser selectByEmail(String email);
    int updateStatus(Long id, String status);
    int updatePassword(Long id, String passwordHash);
    int updateActivationToken(Long id, String token);
    SysUser selectByActivationToken(String token);

    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int deleteUserRoles(@Param("userId") Long userId);

    int deleteById(@Param("id") Long id);
}
