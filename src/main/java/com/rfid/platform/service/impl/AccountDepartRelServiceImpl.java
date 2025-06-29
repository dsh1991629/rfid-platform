package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.AccountDepartmentRelBean;
import com.rfid.platform.mapper.AccountDepartRelMapper;
import com.rfid.platform.service.AccountDepartRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountDepartRelServiceImpl extends ServiceImpl<AccountDepartRelMapper, AccountDepartmentRelBean> implements AccountDepartRelService {

    @Override
    public boolean saveAccountDepartRel(AccountDepartmentRelBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeAccountDepartRelByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateAccountDepartRelByPk(AccountDepartmentRelBean entity) {
        return super.updateById(entity);
    }

    @Override
    public AccountDepartmentRelBean getAccountDepartRelByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<AccountDepartmentRelBean> pageAccountDepartRel(Page<AccountDepartmentRelBean> page, LambdaQueryWrapper<AccountDepartmentRelBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<AccountDepartmentRelBean> listAccountDepartRel(LambdaQueryWrapper<AccountDepartmentRelBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public Boolean removeAccountDepartRelByWrapper(LambdaQueryWrapper<AccountDepartmentRelBean> departRelWrapper) {
        return super.remove(departRelWrapper);
    }
}