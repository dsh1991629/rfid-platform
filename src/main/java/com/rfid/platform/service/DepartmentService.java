package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.DepartmentBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.persistence.DepartmentDTO;

import java.util.List;

public interface DepartmentService {

    boolean saveDepartment(DepartmentBean entity);

    boolean removeDepartmentByPk(Long id);
    
    // 新增：级联删除部门及其所有子部门
    boolean removeDepartmentCascade(Long id);

    boolean updateDepartmentByPk(DepartmentBean entity);

    DepartmentBean getDepartmentByPk(Long id);

    IPage<DepartmentBean> pageDepartment(Page<DepartmentBean> page, LambdaQueryWrapper<DepartmentBean> query);

    List<DepartmentBean> listDepartment(LambdaQueryWrapper<DepartmentBean> queryWrapper);

    Boolean existDepartment(LambdaQueryWrapper<DepartmentBean> nameCheckWrapper);

    DepartmentDTO queryDepartmentByAccountId(Long accountId);
}