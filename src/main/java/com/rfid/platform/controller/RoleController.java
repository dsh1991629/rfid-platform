package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.persistence.RoleCreateDTO;
import com.rfid.platform.persistence.RoleDeleteDTO;
import com.rfid.platform.persistence.RoleUpdateDTO;
import com.rfid.platform.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
     * @param roleCreateDTO 角色创建数据传输对象
     * @return 创建结果，包含角色ID
     */
    @PostMapping(value = "/create")
    @Operation(summary = "创建角色", description = "创建新的角色信息")
    public BaseResult<Long> createRole(
            @Parameter(description = "角色创建信息", required = true)
            @RequestBody RoleCreateDTO roleCreateDTO) {
        BaseResult<Long> result = new BaseResult<>();
        try {
            // 参数校验
            if (StringUtils.isBlank(roleCreateDTO.getName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色名称不能为空");
                return result;
            }

            // 检查角色名称是否已存在
            LambdaQueryWrapper<RoleBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(RoleBean::getName, roleCreateDTO.getName());
            Boolean existingRoles = roleService.existRole(nameCheckWrapper);

            if (existingRoles) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
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
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("创建失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除角色
     * @param roleDeleteDTO 角色删除数据传输对象
     * @return 删除结果
     */
    @PostMapping(value = "/delete")
    @Operation(summary = "删除角色", description = "根据角色ID删除角色信息")
    public BaseResult<Boolean> deleteRole(
            @Parameter(description = "角色删除信息", required = true)
            @RequestBody RoleDeleteDTO roleDeleteDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (roleDeleteDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色ID不能为空");
                return result;
            }

            // 删除角色
            boolean success = roleService.removeRoleByPk(roleDeleteDTO.getId());
            result.setData(success);
            if (success) {
                result.setMessage("删除成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("删除失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新角色
     * @param roleUpdateDTO 角色更新数据传输对象
     * @return 更新结果
     */
    @PostMapping(value = "/update")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public BaseResult<Boolean> updateRole(
            @Parameter(description = "角色更新信息", required = true)
            @RequestBody RoleUpdateDTO roleUpdateDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (roleUpdateDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色ID不能为空");
                return result;
            }

            // 检查部门名称是否已存在（排除当前部门）
            LambdaQueryWrapper<RoleBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(RoleBean::getName, roleUpdateDTO.getName()).ne(RoleBean::getId, roleUpdateDTO.getId());
            Boolean existRoles = roleService.existRole(nameCheckWrapper);

            if (existRoles) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
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
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("更新失败");
                result.setData(false);
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
            result.setData(false);
        }
        return result;
    }

}
