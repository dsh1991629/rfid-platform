package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.RoleMenuRelBean;
import com.rfid.platform.mapper.RoleMenuRelMapper;
import com.rfid.platform.service.RoleMenuRelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMenuRelServiceImpl extends ServiceImpl<RoleMenuRelMapper, RoleMenuRelBean> implements RoleMenuRelService {

    @Override
    public boolean save(RoleMenuRelBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(RoleMenuRelBean entity) {
        return super.updateById(entity);
    }

    @Override
    public RoleMenuRelBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<RoleMenuRelBean> page(Page<RoleMenuRelBean> page, LambdaQueryWrapper<RoleMenuRelBean> query) {
        return super.page(page, query);
    }

    @Override
    public boolean saveRoleMenuRels(List<RoleMenuRelBean> roleMenuRelBeans) {
        return super.saveBatch(roleMenuRelBeans);
    }

    @Override
    public List<RoleMenuRelBean> listRoleMenuRels(LambdaQueryWrapper<RoleMenuRelBean> menuRelWrapper) {
        return super.list(menuRelWrapper);
    }

    @Override
    public boolean removeRoleMenuRels(LambdaQueryWrapper<RoleMenuRelBean> delWrapper) {
        return super.remove(delWrapper);
    }
}