package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.persistence.DepartmentDTO;
import com.rfid.platform.persistence.RoleDTO;

import java.util.List;

public interface AccountService {

    boolean saveAccount(AccountBean entity, DepartmentDTO departmentDTO, RoleDTO roleDTO);

    boolean removeAccountByPk(Long id);

    boolean updateAccountByPk(AccountBean entity, DepartmentDTO departmentDTO, RoleDTO roleDTO);

    AccountBean getAccountByPk(Long id);

    IPage<AccountBean> pageAccount(Page<AccountBean> page, LambdaQueryWrapper<AccountBean> query);

    List<AccountBean> listAccount(LambdaQueryWrapper<AccountBean> queryWrapper);

    String getAccountNameByPk(Long id);

    Boolean existAccount(LambdaQueryWrapper<AccountBean> nameCheckWrapper);
} 