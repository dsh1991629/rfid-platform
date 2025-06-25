package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.MenuBean;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface MenuService {

    boolean saveMenu(MenuBean entity);

    boolean removeMenuByPk(Long id);

    boolean updateMenuByPk(MenuBean entity);

    MenuBean getMenuByPk(Long id);

    IPage<MenuBean> pageMenu(Page<MenuBean> page, LambdaQueryWrapper<MenuBean> query);

    List<MenuBean> listMenu(LambdaQueryWrapper<MenuBean> queryWrapper);

    Boolean existMenu(LambdaQueryWrapper<MenuBean> nameCheckWrapper);
} 