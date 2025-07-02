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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rfid/role")
public class RoleController {

    @Autowired
    private RoleService roleService;


    @PostMapping(value = "/create")
    public BaseResult<Long> createRole(@RequestBody RoleCreateDTO roleCreateDTO) {
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

    @PostMapping(value = "/delete")
    public BaseResult<Boolean> deleteRole(@RequestBody RoleDeleteDTO roleDeleteDTO) {
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

    @PostMapping(value = "/update")
    public BaseResult<Boolean> updateRole(@RequestBody RoleUpdateDTO roleUpdateDTO) {
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
