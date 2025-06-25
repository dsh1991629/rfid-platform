package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface AccountRoleRelService {

    boolean saveAccountRoleRel(AccountRoleRelBean entity);

    boolean removeAccountRoleRelByPk(Long id);

    boolean updateAccountRoleRelByPk(AccountRoleRelBean entity);

    AccountRoleRelBean getAccountRoleRelByPk(Long id);

    IPage<AccountRoleRelBean> pageAccountRoleRel(Page<AccountRoleRelBean> page, LambdaQueryWrapper<AccountRoleRelBean> query);

    List<AccountRoleRelBean> listAccountRoleRel(LambdaQueryWrapper<AccountRoleRelBean> queryWrapper);
}