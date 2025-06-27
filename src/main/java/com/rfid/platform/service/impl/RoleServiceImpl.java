package com.rfid.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.mapper.RoleMapper;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountRoleRelService;
import com.rfid.platform.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleBean> implements RoleService {

    @Autowired
    @Lazy
    private AccountRoleRelService accountRoleRelService;


    @Override
    public boolean saveRole(RoleBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeRoleByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateRoleByPk(RoleBean entity) {
        return super.updateById(entity);
    }

    @Override
    public RoleBean getRoleByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<RoleBean> pageRole(Page<RoleBean> page, LambdaQueryWrapper<RoleBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<RoleBean> listRole(LambdaQueryWrapper<RoleBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public Boolean existRole(LambdaQueryWrapper<RoleBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }

    @Override
    public List<RoleDTO> listRolesByAccountId(Long accountId) {
        LambdaQueryWrapper<AccountRoleRelBean> relWrapper = Wrappers.lambdaQuery();
        relWrapper.eq(AccountRoleRelBean::getAccountId, accountId);
        List<AccountRoleRelBean> accountRoleRelBeans = accountRoleRelService.listAccountRoleRel(relWrapper);
        if (CollectionUtils.isNotEmpty(accountRoleRelBeans)) {
            List<Long> roleIds = accountRoleRelBeans.stream().map(AccountRoleRelBean::getRoleId).collect(Collectors.toUnmodifiableList());
            List<RoleBean> roleBeans = super.listByIds(roleIds);
            if (CollectionUtils.isNotEmpty(roleBeans)) {
                List<RoleDTO> roleDTOS = BeanUtil.copyToList(roleBeans, RoleDTO.class);
                return roleDTOS;
            }
        }
        return List.of();
    }
}