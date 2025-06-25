package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.DepartmentBean;
import com.rfid.platform.mapper.DepartmentMapper;
import com.rfid.platform.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, DepartmentBean> implements DepartmentService {

    @Override
    public boolean saveDepartment(DepartmentBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeDepartmentByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateDepartmentByPk(DepartmentBean entity) {
        return super.updateById(entity);
    }

    @Override
    public DepartmentBean getDepartmentByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<DepartmentBean> pageDepartment(Page<DepartmentBean> page, LambdaQueryWrapper<DepartmentBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<DepartmentBean> listDepartment(LambdaQueryWrapper<DepartmentBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public Boolean existDepartment(LambdaQueryWrapper<DepartmentBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }
}