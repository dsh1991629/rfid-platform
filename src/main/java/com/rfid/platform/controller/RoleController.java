package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.RoleCreateDTO;
import com.rfid.platform.persistence.RoleDeleteDTO;
import com.rfid.platform.persistence.RoleSelectDTO;
import com.rfid.platform.persistence.RoleUpdateDTO;
import com.rfid.platform.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理控制器
 * 提供角色的创建、删除、更新等功能
 */
@RestController
@RequestMapping(value = "/rfid/role")
@Tag(name = "角色管理", description = "角色管理相关接口")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 创建角色
     * @param requestDTO 角色创建数据传输对象
     * @return 创建结果，包含角色ID
     */
    @PostMapping(value = "/create")
    @Operation(summary = "创建角色", description = "创建新的角色信息")
    public RfidApiResponseDTO<Long> createRole(
            @Parameter(description = "角色创建信息", required = true)
            @RequestBody RfidApiRequestDTO<RoleCreateDTO> requestDTO) {
        RfidApiResponseDTO<Long> result = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            result.setStatus(false);
            result.setMessage("请求数据不能为空");
            return result;
        }
        try {
            RoleCreateDTO roleCreateDTO = requestDTO.getData();
            // 参数校验
            if (StringUtils.isBlank(roleCreateDTO.getName())) {
                result.setStatus(false);
                result.setMessage("角色名称不能为空");
                return result;
            }

            // 检查角色名称是否已存在
            LambdaQueryWrapper<RoleBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(RoleBean::getName, roleCreateDTO.getName());
            Boolean existingRoles = roleService.existRole(nameCheckWrapper);

            if (existingRoles) {
                result.setStatus(false);
                result.setMessage("角色名称已存在，不能重复");
                return result;
            }

            // DTO转Bean
            RoleBean roleBean = BeanUtil.copyProperties(roleCreateDTO, RoleBean.class);

            // 保存角色
            boolean success = roleService.saveRole(roleBean, roleCreateDTO.getMenus());
            if (success) {
                result.setData(roleBean.getId());
                result.setMessage("创建成功");
            } else {
                result.setStatus(false);
                result.setMessage("创建失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除角色
     * @param requestDTO 角色删除数据传输对象
     * @return 删除结果
     */
    @PostMapping(value = "/delete")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色信息")
    public RfidApiResponseDTO<Boolean> deleteRole(
            @Parameter(description = "角色删除信息", required = true)
            @RequestBody RfidApiRequestDTO<RoleDeleteDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            result.setStatus(false);
            result.setMessage("请求数据不能为空");
            return result;
        }
        try {
            RoleDeleteDTO roleDeleteDTO = requestDTO.getData();
            // 参数校验
            if (roleDeleteDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("角色ID不能为空");
                return result;
            }

            // 删除角色
            boolean success = roleService.removeRoleByPk(roleDeleteDTO.getId());
            result.setData(success);
            if (success) {
                result.setMessage("删除成功");
            } else {
                result.setStatus(false);
                result.setMessage("删除失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新角色
     * @param requestDTO 角色更新数据传输对象
     * @return 更新结果
     */
    @PostMapping(value = "/update")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public RfidApiResponseDTO<Boolean> updateRole(
            @Parameter(description = "角色更新信息", required = true)
            @RequestBody RfidApiRequestDTO<RoleUpdateDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            result.setStatus(false);
            result.setMessage("请求数据不能为空");
            return result;
        }
        try {
            RoleUpdateDTO roleUpdateDTO = requestDTO.getData();
            // 参数校验
            if (roleUpdateDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("角色ID不能为空");
                return result;
            }

            // 检查部门名称是否已存在（排除当前部门）
            LambdaQueryWrapper<RoleBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(RoleBean::getName, roleUpdateDTO.getName()).ne(RoleBean::getId, roleUpdateDTO.getId());
            Boolean existRoles = roleService.existRole(nameCheckWrapper);

            if (existRoles) {
                result.setStatus(false);
                result.setMessage("角色名称已存在，不能重复");
                result.setData(false);
                return result;
            }

            // DTO转Bean
            RoleBean roleBean = BeanUtil.copyProperties(roleUpdateDTO, RoleBean.class);

            // 更新角色
            boolean success = roleService.updateRoleByPk(roleBean, roleUpdateDTO.getMenus());
            result.setData(success);
            if (success) {
                result.setMessage("更新成功");
            } else {
                result.setStatus(false);
                result.setMessage("更新失败");
                result.setData(false);
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
            result.setData(false);
        }
        return result;
    }



    @PostMapping(value = "/select")
    @Operation(summary = "角色下拉列表", description = "角色下拉列表")
    public RfidApiResponseDTO<List<RoleSelectDTO>> queryRoleSelect() {
        RfidApiResponseDTO<List<RoleSelectDTO>> result = RfidApiResponseDTO.success();
        try {
            List<RoleBean> roleBeans = roleService.listRole(null);
            List<RoleSelectDTO> roleSelects = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(roleBeans)) {
                roleSelects = roleBeans.stream().map(e -> {
                    RoleSelectDTO roleSelectDTO = new RoleSelectDTO();
                    roleSelectDTO.setId(e.getId());
                    roleSelectDTO.setName(e.getName());
                    return roleSelectDTO;
                }).collect(Collectors.toUnmodifiableList());
            }
            result.setData(roleSelects);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

}
