package com.example.drone.controller;

import com.example.drone.domain.dto.DroneDTO;
import com.example.drone.domain.dto.DroneQueryRequest;
import com.example.drone.domain.dto.Page;
import com.example.drone.service.DroneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 无人机前端视图控制器.
 * <p>
 * 提供无人机管理的前端页面路由，使用 Thymeleaf 模板引擎渲染页面。
 * 所有页面均基于 Bootstrap 3.3.7 框架构建。
 * </p>
 *
 * <p>页面路由：
 * <ul>
 *   <li>GET /drones - 无人机列表页面（支持分页和查询）</li>
 *   <li>GET /drones/{id} - 无人机详情页面</li>
 *   <li>GET /drones/new - 创建无人机表单页面</li>
 *   <li>GET /drones/{id}/edit - 编辑无人机表单页面</li>
 * </ul>
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/drones")
public class DroneViewController {

    /**
     * 无人机业务逻辑服务.
     */
    private final DroneService droneService;

    /**
     * 构造函数注入 DroneService.
     *
     * @param droneService 无人机业务逻辑服务
     */
    @Autowired
    public DroneViewController(final DroneService droneService) {
        this.droneService = droneService;
    }

    /**
     * 显示无人机列表页面.
     * <p>
     * 支持分页查询和条件过滤（型号、制造商、状态）。
     * 所有查询参数均为可选，默认显示第一页，每页 20 条记录。
     * </p>
     *
     * <p>URL 示例：
     * <pre>
     * GET /drones
     * GET /drones?modelName=Mavic&manufacturer=DJI&status=AVAILABLE
     * </pre>
     * </p>
     *
     * @param modelName 型号名称（可选，模糊查询）
     * @param manufacturer 制造商（可选，模糊查询）
     * @param status 状态（可选，精确匹配）
     * @param pageNum 页码（可选，默认 1）
     * @param pageSize 每页数量（可选，默认 20）
     * @param model Spring MVC Model 对象
     * @return 列表页面模板名称
     */
    @GetMapping
    public String listDrones(
            @RequestParam(required = false) final String modelName,
            @RequestParam(required = false) final String manufacturer,
            @RequestParam(required = false) final String status,
            @RequestParam(required = false, defaultValue = "1")
            final Integer pageNum,
            @RequestParam(required = false, defaultValue = "5")
            final Integer pageSize,
            final Model model) {

        log.info("访问无人机列表页面: modelName={}, manufacturer={}, "
                + "status={}, pageNum={}, pageSize={}",
                modelName, manufacturer, status, pageNum, pageSize);

        // 构建查询请求
        DroneQueryRequest request = new DroneQueryRequest();
        request.setModelName(modelName);
        request.setManufacturer(manufacturer);
        request.setStatus(status);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);

        // 查询无人机列表
        Page<DroneDTO> page = droneService.listDrones(request);

        // 添加数据到模型（使用模板期望的变量名）
        model.addAttribute("drones", page);
        model.addAttribute("queryRequest", request);

        log.info("无人机列表查询成功: total={}, pageNum={}, pages={}",
                page.getTotal(), page.getPageNum(), page.getPages());

        return "list";
    }

    /**
     * 显示无人机详情页面.
     * <p>
     * 根据无人机 ID 查询并显示完整的无人机信息。
     * 如果无人机不存在，将抛出 DroneNotFoundException，
     * 由全局异常处理器处理并返回错误页面。
     * </p>
     *
     * <p>URL 示例：
     * <pre>
     * GET /drones/1
     * </pre>
     * </p>
     *
     * @param id 无人机 ID
     * @param model Spring MVC Model 对象
     * @return 详情页面模板名称
     */
    @GetMapping("/{id}")
    public String getDroneDetail(@PathVariable final Long id,
                                  final Model model) {
        log.info("访问无人机详情页面: id={}", id);

        // 查询无人机详情
        DroneDTO drone = droneService.getDroneById(id);

        // 添加数据到模型
        model.addAttribute("drone", drone);

        log.info("无人机详情查询成功: serialNumber={}",
                drone.getSerialNumber());

        return "detail";
    }

    /**
     * 显示创建无人机表单页面.
     * <p>
     * 返回一个空白表单，用于录入新的无人机信息。
     * 表单提交后将通过 REST API（POST /api/v1/drones）创建无人机。
     * </p>
     *
     * <p>URL 示例：
     * <pre>
     * GET /drones/new
     * </pre>
     * </p>
     *
     * @param model Spring MVC Model 对象
     * @return 表单页面模板名称
     */
    @GetMapping("/new")
    public String showCreateForm(final Model model) {
        log.info("访问创建无人机表单页面");

        // 添加表单模式标识（使用模板期望的变量名）
        model.addAttribute("isEdit", false);

        return "form";
    }

    /**
     * 显示编辑无人机表单页面.
     * <p>
     * 根据无人机 ID 查询现有数据，并填充到表单中供用户编辑。
     * 表单提交后将通过 REST API（PUT /api/v1/drones/{id}）更新无人机。
     * </p>
     *
     * <p>URL 示例：
     * <pre>
     * GET /drones/1/edit
     * </pre>
     * </p>
     *
     * @param id 无人机 ID
     * @param model Spring MVC Model 对象
     * @return 表单页面模板名称
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable final Long id,
                               final Model model) {
        log.info("访问编辑无人机表单页面: id={}", id);

        // 查询无人机详情
        DroneDTO drone = droneService.getDroneById(id);

        // 添加数据到模型（使用模板期望的变量名）
        model.addAttribute("isEdit", true);
        model.addAttribute("drone", drone);

        log.info("无人机数据加载成功: serialNumber={}",
                drone.getSerialNumber());

        return "form";
    }
}
