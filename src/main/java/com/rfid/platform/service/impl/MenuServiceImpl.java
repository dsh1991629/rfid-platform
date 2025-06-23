package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.MenuBean;
import com.rfid.platform.mapper.MenuMapper;
import com.rfid.platform.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuBean> implements MenuService {

    @Override
    public boolean save(MenuBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateById(MenuBean entity) {
        return super.updateById(entity);
    }

    @Override
    public MenuBean getById(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<MenuBean> page(Page<MenuBean> page, LambdaQueryWrapper<MenuBean> query) {
        return super.page(page, query);
    }
} 