package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.mapper.RoleMapper;
import com.rfid.platform.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleBean> implements RoleService {

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
}