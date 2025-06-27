package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.RoleBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.RoleDTO;

import java.util.List;

public interface RoleService {

    boolean saveRole(RoleBean entity, List<MenuDTO> menuDTOS);

    boolean removeRoleByPk(Long id);

    boolean updateRoleByPk(RoleBean entity, List<MenuDTO> menuDTOS);

    RoleBean getRoleByPk(Long id);

    IPage<RoleBean> pageRole(Page<RoleBean> page, LambdaQueryWrapper<RoleBean> query);

    List<RoleBean> listRole(LambdaQueryWrapper<RoleBean> queryWrapper);

    Boolean existRole(LambdaQueryWrapper<RoleBean> nameCheckWrapper);

    RoleDTO queryRoleByAccountId(Long accountId);
}