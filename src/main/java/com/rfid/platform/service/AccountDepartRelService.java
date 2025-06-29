package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountDepartmentRelBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface AccountDepartRelService {

    boolean saveAccountDepartRel(AccountDepartmentRelBean entity);

    boolean removeAccountDepartRelByPk(Long id);

    boolean updateAccountDepartRelByPk(AccountDepartmentRelBean entity);

    AccountDepartmentRelBean getAccountDepartRelByPk(Long id);

    IPage<AccountDepartmentRelBean> pageAccountDepartRel(Page<AccountDepartmentRelBean> page, LambdaQueryWrapper<AccountDepartmentRelBean> query);

    List<AccountDepartmentRelBean> listAccountDepartRel(LambdaQueryWrapper<AccountDepartmentRelBean> queryWrapper);

    Boolean removeAccountDepartRelByWrapper(LambdaQueryWrapper<AccountDepartmentRelBean> departRelWrapper);
}