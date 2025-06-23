package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.rfid.platform.mapper.AccountRoleRelMapper;
import com.rfid.platform.service.AccountRoleRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class AccountRoleRelServiceImpl extends ServiceImpl<AccountRoleRelMapper, AccountRoleRelBean> implements AccountRoleRelService {

    @Override
    public boolean save(AccountRoleRelBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(AccountRoleRelBean entity) {
        return super.updateById(entity);
    }

    @Override
    public AccountRoleRelBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<AccountRoleRelBean> page(Page<AccountRoleRelBean> page, LambdaQueryWrapper<AccountRoleRelBean> query) {
        return super.page(page, query);
    }
} 