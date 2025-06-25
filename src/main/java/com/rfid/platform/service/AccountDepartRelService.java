package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountDepartRelBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface AccountDepartRelService {

    boolean saveAccountDepartRel(AccountDepartRelBean entity);

    boolean removeAccountDepartRelByPk(Long id);

    boolean updateAccountDepartRelByPk(AccountDepartRelBean entity);

    AccountDepartRelBean getAccountDepartRelByPk(Long id);

    IPage<AccountDepartRelBean> pageAccountDepartRel(Page<AccountDepartRelBean> page, LambdaQueryWrapper<AccountDepartRelBean> query);

    List<AccountDepartRelBean> listAccountDepartRel(LambdaQueryWrapper<AccountDepartRelBean> queryWrapper);
}