package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface AccountService {

    boolean save(AccountBean entity);

    boolean removeById(Long id);

    boolean updateById(AccountBean entity);

    AccountBean getById(Long id);

    IPage<AccountBean> page(Page<AccountBean> page, LambdaQueryWrapper<AccountBean> query);
} 