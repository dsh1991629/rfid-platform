package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface AccountService {

    boolean saveAccount(AccountBean entity);

    boolean removeAccountByPk(Long id);

    boolean updateAccountByPk(AccountBean entity);

    AccountBean getAccountByPk(Long id);

    IPage<AccountBean> pageAccount(Page<AccountBean> page, LambdaQueryWrapper<AccountBean> query);

    List<AccountBean> listAccount(LambdaQueryWrapper<AccountBean> queryWrapper);

    String getAccountNameByPk(Long id);

    Boolean existAccount(LambdaQueryWrapper<AccountBean> nameCheckWrapper);
} 