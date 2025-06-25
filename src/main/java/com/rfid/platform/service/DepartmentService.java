package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.DepartmentBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface DepartmentService {

    boolean saveDepartment(DepartmentBean entity);

    boolean removeDepartmentByPk(Long id);

    boolean updateDepartmentByPk(DepartmentBean entity);

    DepartmentBean getDepartmentByPk(Long id);

    IPage<DepartmentBean> pageDepartment(Page<DepartmentBean> page, LambdaQueryWrapper<DepartmentBean> query);

    List<DepartmentBean> listDepartment(LambdaQueryWrapper<DepartmentBean> queryWrapper);

    Boolean existDepartment(LambdaQueryWrapper<DepartmentBean> nameCheckWrapper);
}