package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.DepartmentBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DepartmentService {

    boolean save(DepartmentBean entity);

    boolean removeById(Long id);

    boolean updateById(DepartmentBean entity);

    DepartmentBean getById(Long id);

    IPage<DepartmentBean> page(Page<DepartmentBean> page, LambdaQueryWrapper<DepartmentBean> query);
} 