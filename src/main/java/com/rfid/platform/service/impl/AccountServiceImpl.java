package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.mapper.AccountMapper;
import com.rfid.platform.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountBean> implements AccountService {

    @Override
    public boolean saveAccount(AccountBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeAccountByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateAccountByPk(AccountBean entity) {
        return super.updateById(entity);
    }

    @Override
    public AccountBean getAccountByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<AccountBean> pageAccount(Page<AccountBean> page, LambdaQueryWrapper<AccountBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<AccountBean> listAccount(LambdaQueryWrapper<AccountBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public String getAccountNameByPk(Long id) {
        return Optional.ofNullable(super.getById(id))
                .map(AccountBean::getName)
                .orElse("");
    }


    @Override
    public Boolean existAccount(LambdaQueryWrapper<AccountBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }
}