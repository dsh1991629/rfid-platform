package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AccountRoleRelService {

    boolean save(AccountRoleRelBean entity);

    boolean removeById(Long id);

    boolean updateById(AccountRoleRelBean entity);

    AccountRoleRelBean getById(Long id);

    IPage<AccountRoleRelBean> page(Page<AccountRoleRelBean> page, LambdaQueryWrapper<AccountRoleRelBean> query);
} 