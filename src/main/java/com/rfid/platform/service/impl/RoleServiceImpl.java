package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.mapper.RoleMapper;
import com.rfid.platform.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleBean> implements RoleService {

    @Override
    public boolean save(RoleBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(RoleBean entity) {
        return super.updateById(entity);
    }

    @Override
    public RoleBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<RoleBean> page(Page<RoleBean> page, LambdaQueryWrapper<RoleBean> query) {
        return super.page(page, query);
    }
} 