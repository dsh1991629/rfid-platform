package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.persistence.AccountPageDepartmentDTO;
import com.rfid.platform.persistence.AccountPageRoleDTO;
import com.rfid.platform.persistence.DepartmentDTO;
import com.rfid.platform.persistence.RoleDTO;

import java.util.List;

public interface AccountService {

    boolean saveAccount(AccountBean entity, AccountPageDepartmentDTO department, AccountPageRoleDTO role);

    boolean removeAccountByPk(Long id);

    boolean updateAccountByPk(AccountBean entity, AccountPageDepartmentDTO department, AccountPageRoleDTO role);

    AccountBean getAccountByPk(Long id);

    IPage<AccountBean> pageAccount(Page<AccountBean> page, LambdaQueryWrapper<AccountBean> query, Long departmentId, Long roleId);

    List<AccountBean> listAccount(LambdaQueryWrapper<AccountBean> queryWrapper);

    String getAccountNameByPk(Long id);

    Boolean existAccount(LambdaQueryWrapper<AccountBean> nameCheckWrapper);
} 