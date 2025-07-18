package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.entity.AccountDepartmentRelBean;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.rfid.platform.mapper.AccountMapper;
import com.rfid.platform.persistence.AccountPageDepartmentDTO;
import com.rfid.platform.persistence.AccountPageRoleDTO;
import com.rfid.platform.service.AccountDepartRelService;
import com.rfid.platform.service.AccountRoleRelService;
import com.rfid.platform.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.service.RoleMenuRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountBean> implements AccountService {

    @Autowired
    @Lazy
    private AccountRoleRelService accountRoleRelService;

    @Autowired
    @Lazy
    private AccountDepartRelService accountDepartRelService;

    @Autowired
    @Lazy
    private RoleMenuRelService roleMenuRelService;


    @Transactional
    @Override
    public boolean saveAccount(AccountBean entity, AccountPageDepartmentDTO department, AccountPageRoleDTO role) {
        super.save(entity);
        Long accountId = entity.getId();

        if (Objects.nonNull(department) && Objects.nonNull(department.getId())) {
            AccountDepartmentRelBean accountDepartmentRelBean = new AccountDepartmentRelBean();
            accountDepartmentRelBean.setAccountId(accountId);
            accountDepartmentRelBean.setDepartmentId(department.getId());
            accountDepartRelService.saveAccountDepartRel(accountDepartmentRelBean);
        }

        if (Objects.nonNull(role) && Objects.nonNull(role.getId())) {
            AccountRoleRelBean accountRoleRelBean = new AccountRoleRelBean();
            accountRoleRelBean.setAccountId(accountId);
            accountRoleRelBean.setRoleId(role.getId());
            accountRoleRelService.saveAccountRoleRel(accountRoleRelBean);
        }

        return true;
    }

    @Transactional
    @Override
    public boolean removeAccountByPk(Long id) {
        LambdaQueryWrapper<AccountRoleRelBean> delWrapper = Wrappers.lambdaQuery();
        delWrapper.eq(AccountRoleRelBean::getAccountId, id);
        accountRoleRelService.removeAccountRoleRelByWrapper(delWrapper);

        LambdaQueryWrapper<AccountDepartmentRelBean> departRelWrapper = Wrappers.lambdaQuery();
        departRelWrapper.eq(AccountDepartmentRelBean::getAccountId, id);
        accountDepartRelService.removeAccountDepartRelByWrapper(departRelWrapper);

        return super.removeById(id);
    }

    @Transactional
    @Override
    public boolean updateAccountByPk(AccountBean entity, AccountPageDepartmentDTO department, AccountPageRoleDTO role) {
        super.updateById(entity);

        if (Objects.nonNull(department) && Objects.nonNull(department.getId())) {
            LambdaQueryWrapper<AccountDepartmentRelBean> departRelWrapper = Wrappers.lambdaQuery();
            departRelWrapper.eq(AccountDepartmentRelBean::getAccountId, entity.getId());
            accountDepartRelService.removeAccountDepartRelByWrapper(departRelWrapper);

            AccountDepartmentRelBean accountDepartmentRelBean = new AccountDepartmentRelBean();
            accountDepartmentRelBean.setAccountId(entity.getId());
            accountDepartmentRelBean.setDepartmentId(department.getId());
            accountDepartRelService.saveAccountDepartRel(accountDepartmentRelBean);
        }

        if (Objects.nonNull(role) && Objects.nonNull(role.getId())) {
            LambdaQueryWrapper<AccountRoleRelBean> roleRelWrapper = Wrappers.lambdaQuery();
            roleRelWrapper.eq(AccountRoleRelBean::getAccountId, entity.getId());
            accountRoleRelService.removeAccountRoleRelByWrapper(roleRelWrapper);

            AccountRoleRelBean accountRoleRelBean = new AccountRoleRelBean();
            accountRoleRelBean.setAccountId(entity.getId());
            accountRoleRelBean.setRoleId(role.getId());
            accountRoleRelService.saveAccountRoleRel(accountRoleRelBean);
        }

        return true;
    }

    @Override
    public AccountBean getAccountByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<AccountBean> pageAccount(Page<AccountBean> page, LambdaQueryWrapper<AccountBean> query, Long departmentId, Long roleId) {
        return super.getBaseMapper().queryAccountPage(page, query, departmentId, roleId);
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

    @Override
    public List<AccountBean> listAccountByIds(List<Long> accountIds) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return List.of();
        }
        LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AccountBean::getId, accountIds);
        return super.list(queryWrapper);
    }


    @Override
    public List<AccountBean> listAccountByCode(String code) {
        LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBean::getCode, code);
        return super.list(queryWrapper);
    }
}