package com.example.drone.repository;

import com.example.drone.domain.entity.SysUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_user_role WHERE user_id=#{userId}")
    int deleteUserRoles(@Param("userId") Long userId);

    @Delete("DELETE FROM sys_user WHERE id=#{id}")
    int deleteById(@Param("id") Long id);
}
