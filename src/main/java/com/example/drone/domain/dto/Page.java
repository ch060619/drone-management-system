package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 DTO.
 * <p>
 * 用于封装分页查询的结果，包含总记录数、当前页码、每页数量、总页数和数据列表。
 * </p>
 *
 * @param <T> 数据类型
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

    /**
     * 总记录数.
     */
    private Long total;

    /**
     * 当前页码（从 1 开始）.
     */
    private Integer pageNum;

    /**
     * 每页记录数.
     */
    private Integer pageSize;

    /**
     * 总页数.
     */
    private Integer pages;

    /**
     * 当前页的数据列表.
     */
    private List<T> list;
}
