package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.RoleBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface RoleService {

    boolean save(RoleBean entity);

    boolean removeById(Long id);

    boolean updateById(RoleBean entity);

    RoleBean getById(Long id);

    IPage<RoleBean> page(Page<RoleBean> page, LambdaQueryWrapper<RoleBean> query);
} 