package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountDepartRelBean;
import com.rfid.platform.mapper.AccountDepartRelMapper;
import com.rfid.platform.service.AccountDepartRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class AccountDepartRelServiceImpl extends ServiceImpl<AccountDepartRelMapper, AccountDepartRelBean> implements AccountDepartRelService {

    @Override
    public boolean save(AccountDepartRelBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(AccountDepartRelBean entity) {
        return super.updateById(entity);
    }

    @Override
    public AccountDepartRelBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<AccountDepartRelBean> page(Page<AccountDepartRelBean> page, LambdaQueryWrapper<AccountDepartRelBean> query) {
        return super.page(page, query);
    }
} 