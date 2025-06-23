package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.DepartmentBean;
import com.rfid.platform.mapper.DepartmentMapper;
import com.rfid.platform.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, DepartmentBean> implements DepartmentService {

    @Override
    public boolean save(DepartmentBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(DepartmentBean entity) {
        return super.updateById(entity);
    }

    @Override
    public DepartmentBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<DepartmentBean> page(Page<DepartmentBean> page, LambdaQueryWrapper<DepartmentBean> query) {
        return super.page(page, query);
    }
} 