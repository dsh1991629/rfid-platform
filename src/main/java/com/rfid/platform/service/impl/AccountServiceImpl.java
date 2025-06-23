package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.mapper.AccountMapper;
import com.rfid.platform.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountBean> implements AccountService {

    @Override
    public boolean save(AccountBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(AccountBean entity) {
        return super.updateById(entity);
    }

    @Override
    public AccountBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<AccountBean> page(Page<AccountBean> page, LambdaQueryWrapper<AccountBean> query) {
        // 简单分页，实际可根据query自定义条件
        return super.page(page, query);
    }
} 