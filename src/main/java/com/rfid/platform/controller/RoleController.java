package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PageResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.persistence.SelectDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.MenuService;
import com.rfid.platform.service.RoleService;
import com.rfid.platform.util.TimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rfid/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MenuService menuService;


    @PostMapping(value = "/create")
    public BaseResult<Long> createRole(@RequestBody RoleDTO roleDTO) {
        BaseResult<Long> result = new BaseResult<>();
        try {
            // 参数校验
            if (StringUtils.isBlank(roleDTO.getName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色名称不能为空");
                return result;
            }

            // 检查角色名称是否已存在
            LambdaQueryWrapper<RoleBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(RoleBean::getName, roleDTO.getName());
            Boolean existingRoles = roleService.existRole(nameCheckWrapper);

            if (existingRoles) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色名称已存在，不能重复");
                return result;
            }

            // DTO转Bean
            RoleBean roleBean = BeanUtil.copyProperties(roleDTO, RoleBean.class);

            // 保存角色
            boolean success = roleService.saveRole(roleBean, roleDTO.getMenus());
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
    public BaseResult<Boolean> deleteRole(@RequestBody RoleDTO roleDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (roleDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色ID不能为空");
                return result;
            }

            // 删除角色
            boolean success = roleService.removeRoleByPk(roleDTO.getId());
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
    public BaseResult<Boolean> updateRole(@RequestBody RoleDTO roleDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (roleDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色ID不能为空");
                return result;
            }

            // 检查部门名称是否已存在（排除当前部门）
            LambdaQueryWrapper<RoleBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(RoleBean::getName, roleDTO.getName()).ne(RoleBean::getId, roleDTO.getId());
            Boolean existRoles = roleService.existRole(nameCheckWrapper);

            if (existRoles) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("角色名称已存在，不能重复");
                result.setData(false);
                return result;
            }

            // DTO转Bean
            RoleBean roleBean = BeanUtil.copyProperties(roleDTO, RoleBean.class);

            // 更新角色
            boolean success = roleService.updateRoleByPk(roleBean, roleDTO.getMenus());
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


    @PostMapping(value = "/page")
    public BaseResult<PageResult<RoleDTO>> queryRolePage(@RequestBody RoleDTO roleDTO,
                                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        BaseResult<PageResult<RoleDTO>> result = new BaseResult<>();
        try {
            // 构建分页对象
            Page<RoleBean> page = new Page<>(pageNum, pageSize);

            // 构建查询条件
            LambdaQueryWrapper<RoleBean> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(roleDTO.getName())) {
                queryWrapper.like(RoleBean::getName, roleDTO.getName());
            }
            if (roleDTO.getId() != null) {
                queryWrapper.eq(RoleBean::getId, roleDTO.getId());
            }
            queryWrapper.orderByDesc(RoleBean::getCreateTime);

            // 执行分页查询
            IPage<RoleBean> pageResult = roleService.pageRole(page, queryWrapper);

            // 转换结果
            PageResult<RoleDTO> pageResultDTO = new PageResult<>();
            pageResultDTO.setPageNum(pageNum);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotal(pageResult.getTotal());
            pageResultDTO.setPages(pageResult.getPages());

            List<RoleDTO> roleDTOList = pageResult.getRecords().stream().map(roleBean -> {
                RoleDTO dto = BeanUtil.copyProperties(roleBean, RoleDTO.class);
                // 格式化创建时间
                if (roleBean.getCreateTime() != null) {
                    dto.setCreateDate(TimeUtil.getDateFormatterString(roleBean.getCreateTime()));
                }
                if (Objects.nonNull(roleBean.getCreateId())) {
                    dto.setCreateAccountName(accountService.getAccountNameByPk(roleBean.getCreateId()));
                }

                List<MenuDTO> menuDTOS = menuService.queryMenusByRole(roleBean.getId());
                if (CollectionUtils.isNotEmpty(menuDTOS)) {
                    dto.setMenus(menuDTOS);
                }

                return dto;
            }).collect(Collectors.toUnmodifiableList());

            pageResultDTO.setData(roleDTOList);
            result.setData(pageResultDTO);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/select")
    public BaseResult<List<SelectDTO>> queryRoleSelect() {
        BaseResult<List<SelectDTO>> result = new BaseResult<>();
        try {
            // 查询所有角色
            LambdaQueryWrapper<RoleBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(RoleBean::getName);
            List<RoleBean> roleBeanList = roleService.listRole(queryWrapper);

            // 转换为SelectDTO
            List<SelectDTO> selectDTOList = roleBeanList.stream().map(roleBean -> {
                SelectDTO selectDTO = new SelectDTO();
                selectDTO.setId(roleBean.getId());
                selectDTO.setName(roleBean.getName());
                return selectDTO;
            }).collect(Collectors.toUnmodifiableList());

            result.setData(selectDTOList);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }


    @PostMapping(value = "/menus")
    public BaseResult<List<MenuDTO>> queryRoleSelect(@RequestBody RoleDTO roleDTO) {
        BaseResult<List<MenuDTO>> result = new BaseResult<>();

        // 参数校验
        if (roleDTO.getId() == null) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("角色ID不能为空");
            return result;
        }
        List<MenuDTO> menuDTOS = menuService.queryMenusByRole(roleDTO.getId());
        result.setData(menuDTOS);
        return result;
    }

}
