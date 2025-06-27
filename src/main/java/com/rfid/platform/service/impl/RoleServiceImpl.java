package com.rfid.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.entity.RoleMenuRelBean;
import com.rfid.platform.mapper.RoleMapper;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountRoleRelService;
import com.rfid.platform.service.RoleMenuRelService;
import com.rfid.platform.service.RoleService;
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
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleBean> implements RoleService {

    @Autowired
    @Lazy
    private AccountRoleRelService accountRoleRelService;

    @Autowired
    @Lazy
    private RoleMenuRelService roleMenuRelService;


    @Transactional
    @Override
    public boolean saveRole(RoleBean entity, List<MenuDTO> menuDTOS) {
        super.save(entity);
        if (CollectionUtils.isNotEmpty(menuDTOS)) {
            List<RoleMenuRelBean> roleMenuRelBeans = menuDTOS.stream().map(e -> {
                RoleMenuRelBean roleMenuRelBean = new RoleMenuRelBean();
                roleMenuRelBean.setRoleId(entity.getId());
                roleMenuRelBean.setMenuId(e.getId());
                return roleMenuRelBean;
            }).collect(Collectors.toUnmodifiableList());
            roleMenuRelService.saveRoleMenuRels(roleMenuRelBeans);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean removeRoleByPk(Long id) {
        super.removeById(id);
        LambdaQueryWrapper<RoleMenuRelBean> delWrapper = Wrappers.lambdaQuery();
        delWrapper.eq(RoleMenuRelBean::getRoleId, id);
        roleMenuRelService.removeRoleMenuRels(delWrapper);
        return true;
    }

    @Transactional
    @Override
    public boolean updateRoleByPk(RoleBean entity, List<MenuDTO> menuDTOS) {
        super.updateById(entity);

        if (CollectionUtils.isNotEmpty(menuDTOS)) {
            LambdaQueryWrapper<RoleMenuRelBean> delWrapper = Wrappers.lambdaQuery();
            delWrapper.eq(RoleMenuRelBean::getRoleId, entity.getId());
            roleMenuRelService.removeRoleMenuRels(delWrapper);

            List<RoleMenuRelBean> roleMenuRelBeans = menuDTOS.stream().map(e -> {
                RoleMenuRelBean roleMenuRelBean = new RoleMenuRelBean();
                roleMenuRelBean.setRoleId(entity.getId());
                roleMenuRelBean.setMenuId(e.getId());
                return roleMenuRelBean;
            }).collect(Collectors.toUnmodifiableList());
            roleMenuRelService.saveRoleMenuRels(roleMenuRelBeans);
        }
        return true;
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
    public RoleDTO queryRoleByAccountId(Long accountId) {
        LambdaQueryWrapper<AccountRoleRelBean> roleRelWrapper = Wrappers.lambdaQuery();
        roleRelWrapper.eq(AccountRoleRelBean::getAccountId, accountId);
        List<AccountRoleRelBean> accountRoleRelBeans = accountRoleRelService.listAccountRoleRel(roleRelWrapper);
        if (CollectionUtils.isEmpty(accountRoleRelBeans)) {
            return null;
        }
        AccountRoleRelBean accountRoleRelBean = accountRoleRelBeans.get(0);
        Long roleId = accountRoleRelBean.getRoleId();
        RoleBean roleBean = super.getById(roleId);
        if (Objects.isNull(roleBean)) {
            return null;
        }
        RoleDTO roleDTO = BeanUtil.copyProperties(roleBean, RoleDTO.class);
        return roleDTO;
    }
}