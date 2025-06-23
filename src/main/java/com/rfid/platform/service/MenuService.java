package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.MenuBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface MenuService {

    boolean save(MenuBean entity);

    boolean removeById(Long id);

    boolean updateById(MenuBean entity);

    MenuBean getById(Long id);

    IPage<MenuBean> page(Page<MenuBean> page, LambdaQueryWrapper<MenuBean> query);
} 