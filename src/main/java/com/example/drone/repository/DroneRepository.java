package com.example.drone.repository;

import com.example.drone.domain.dto.DroneQueryConditions;
import com.example.drone.domain.entity.Drone;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 无人机数据访问接口.
 * <p>
 * 使用 MyBatis 框架实现数据库 CRUD 操作，支持 SQLite 和 MySQL 两种数据库。
 * 具体的 SQL 映射定义在 mapper/sqlite/DroneMapper.xml 和 mapper/mysql/DroneMapper.xml 中。
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Mapper
public interface DroneRepository {

    /**
     * 插入新的无人机记录.
     * <p>
     * 插入成功后，会自动将生成的主键 ID 回填到 drone 对象的 id 字段中。
     * </p>
     *
     * @param drone 无人机实体对象，包含除 id、createdAt、updatedAt 外的所有字段
     * @return 受影响的行数（成功插入返回 1）
     */
    int insert(Drone drone);

    /**
     * 根据主键 ID 查询无人机.
     *
     * @param id 无人机主键 ID
     * @return 无人机实体对象，如果不存在则返回 null
     */
    Drone selectById(Long id);

    /**
     * 根据序列号查询无人机.
     * <p>
     * 序列号在数据库中具有唯一约束，因此最多返回一条记录。
     * </p>
     *
     * @param serialNumber 无人机序列号
     * @return 无人机实体对象，如果不存在则返回 null
     */
    Drone selectBySerialNumber(String serialNumber);

    /**
     * 根据条件查询无人机列表.
     * <p>
     * 支持按型号、制造商、状态进行筛选，支持分页查询。
     * 如果 conditions 中的某个字段为 null，则不作为查询条件。
     * </p>
     *
     * @param conditions 查询条件对象，包含 modelName、manufacturer、status、offset、limit
     * @return 符合条件的无人机列表，如果没有符合条件的记录则返回空列表
     */
    List<Drone> selectByConditions(DroneQueryConditions conditions);

    /**
     * 统计符合条件的无人机数量.
     * <p>
     * 用于分页查询时计算总记录数。
     * 查询条件与 selectByConditions 方法一致（不包含 offset 和 limit）。
     * </p>
     *
     * @param conditions 查询条件对象，包含 modelName、manufacturer、status
     * @return 符合条件的记录总数
     */
    int countByConditions(DroneQueryConditions conditions);

    /**
     * 根据主键 ID 更新无人机信息.
     * <p>
     * 只更新 drone 对象中非 null 的字段。
     * id、createdAt 字段不会被更新，updatedAt 字段由数据库自动更新。
     * </p>
     *
     * @param drone 无人机实体对象，必须包含 id 字段
     * @return 受影响的行数（成功更新返回 1，记录不存在返回 0）
     */
    int updateById(Drone drone);

    /**
     * 根据主键 ID 删除无人机.
     *
     * @param id 无人机主键 ID
     * @return 受影响的行数（成功删除返回 1，记录不存在返回 0）
     */
    int deleteById(Long id);
}
