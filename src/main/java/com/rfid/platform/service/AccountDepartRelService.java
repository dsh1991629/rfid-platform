package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountDepartRelBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AccountDepartRelService {

    boolean save(AccountDepartRelBean entity);

    boolean removeById(Long id);

    boolean updateById(AccountDepartRelBean entity);

    AccountDepartRelBean getById(Long id);

    IPage<AccountDepartRelBean> page(Page<AccountDepartRelBean> page, LambdaQueryWrapper<AccountDepartRelBean> query);
} 