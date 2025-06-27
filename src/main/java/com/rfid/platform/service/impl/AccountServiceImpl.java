package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.entity.AccountDepartRelBean;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.rfid.platform.mapper.AccountMapper;
import com.rfid.platform.service.AccountDepartRelService;
import com.rfid.platform.service.AccountRoleRelService;
import com.rfid.platform.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountBean> implements AccountService {

    @Autowired
    @Lazy
    private AccountRoleRelService accountRoleRelService;

    @Autowired
    @Lazy
    private AccountDepartRelService accountDepartRelService;


    @Transactional
    @Override
    public boolean saveAccount(AccountBean entity, Long departmentId, List<Long> roleIds) {
        super.save(entity);
        Long accountId = entity.getId();

        if (Objects.nonNull(departmentId)) {
            AccountDepartRelBean accountDepartRelBean = new AccountDepartRelBean();
            accountDepartRelBean.setAccountId(accountId);
            accountDepartRelBean.setDepartmentId(departmentId);
            accountDepartRelService.saveAccountDepartRel(accountDepartRelBean);
        }

        if (CollectionUtils.isNotEmpty(roleIds)) {
            List<AccountRoleRelBean> accountRoleRelBeans = roleIds.stream().map(e -> {
                AccountRoleRelBean accountRoleRelBean = new AccountRoleRelBean();
                accountRoleRelBean.setAccountId(accountId);
                accountRoleRelBean.setRoleId(e);
                return accountRoleRelBean;
            }).collect(Collectors.toUnmodifiableList());
            accountRoleRelService.saveAccountRoleRels(accountRoleRelBeans);
        }

        return true;
    }

    @Transactional
    @Override
    public boolean removeAccountByPk(Long id) {
        LambdaQueryWrapper<AccountRoleRelBean> delWrapper = Wrappers.lambdaQuery();
        delWrapper.eq(AccountRoleRelBean::getAccountId, id);
        accountRoleRelService.removeAccountRoleRelByWrapper(delWrapper);

        LambdaQueryWrapper<AccountDepartRelBean> departRelWrapper = Wrappers.lambdaQuery();
        departRelWrapper.eq(AccountDepartRelBean::getAccountId, id);
        accountDepartRelService.removeAccountDepartRelByWrapper(departRelWrapper);

        return super.removeById(id);
    }

    @Transactional
    @Override
    public boolean updateAccountByPk(AccountBean entity, Long departmentId, List<Long> roleIds) {
        super.updateById(entity);

        if (Objects.nonNull(departmentId)) {
            LambdaQueryWrapper<AccountDepartRelBean> departRelWrapper = Wrappers.lambdaQuery();
            departRelWrapper.eq(AccountDepartRelBean::getAccountId, entity.getId());
            accountDepartRelService.removeAccountDepartRelByWrapper(departRelWrapper);

            AccountDepartRelBean accountDepartRelBean = new AccountDepartRelBean();
            accountDepartRelBean.setAccountId(entity.getId());
            accountDepartRelBean.setDepartmentId(departmentId);
            accountDepartRelService.saveAccountDepartRel(accountDepartRelBean);
        }

        if (CollectionUtils.isNotEmpty(roleIds)) {
            LambdaQueryWrapper<AccountRoleRelBean> roleRelWrapper = Wrappers.lambdaQuery();
            roleRelWrapper.eq(AccountRoleRelBean::getAccountId, entity.getId());
            accountRoleRelService.removeAccountRoleRelByWrapper(roleRelWrapper);

            List<AccountRoleRelBean> accountRoleRelBeans = roleIds.stream().map(e -> {
                AccountRoleRelBean accountRoleRelBean = new AccountRoleRelBean();
                accountRoleRelBean.setAccountId(entity.getId());
                accountRoleRelBean.setRoleId(e);
                return accountRoleRelBean;
            }).collect(Collectors.toUnmodifiableList());
            accountRoleRelService.saveAccountRoleRels(accountRoleRelBeans);
        }

        return true;
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