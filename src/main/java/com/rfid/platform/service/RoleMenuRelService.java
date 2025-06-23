package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.RoleMenuRelBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface RoleMenuRelService {

    boolean save(RoleMenuRelBean entity);

    boolean removeById(Long id);

    boolean updateById(RoleMenuRelBean entity);

    RoleMenuRelBean getById(Long id);

    IPage<RoleMenuRelBean> page(Page<RoleMenuRelBean> page, LambdaQueryWrapper<RoleMenuRelBean> query);
} 