package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.rfid.platform.mapper.AccountRoleRelMapper;
import com.rfid.platform.service.AccountRoleRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountRoleRelServiceImpl extends ServiceImpl<AccountRoleRelMapper, AccountRoleRelBean> implements AccountRoleRelService {

    @Override
    public boolean saveAccountRoleRel(AccountRoleRelBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeAccountRoleRelByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateAccountRoleRelByPk(AccountRoleRelBean entity) {
        return super.updateById(entity);
    }

    @Override
    public AccountRoleRelBean getAccountRoleRelByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<AccountRoleRelBean> pageAccountRoleRel(Page<AccountRoleRelBean> page, LambdaQueryWrapper<AccountRoleRelBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<AccountRoleRelBean> listAccountRoleRel(LambdaQueryWrapper<AccountRoleRelBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public Boolean removeAccountRoleRelByWrapper(LambdaQueryWrapper<AccountRoleRelBean> delWrapper) {
        return super.remove(delWrapper);
    }

    @Override
    public Boolean saveAccountRoleRels(List<AccountRoleRelBean> accountRoleRelBeans) {
        return super.saveBatch(accountRoleRelBeans);
    }
}