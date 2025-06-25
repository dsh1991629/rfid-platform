package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.MenuBean;
import com.rfid.platform.mapper.MenuMapper;
import com.rfid.platform.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuBean> implements MenuService {

    @Override
    public boolean saveMenu(MenuBean entity) {
        return super.save(entity);
    }

    @Override
    public boolean removeMenuByPk(Long id) {
        return super.removeById(id);
    }

    @Override
    public boolean updateMenuByPk(MenuBean entity) {
        return super.updateById(entity);
    }

    @Override
    public MenuBean getMenuByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<MenuBean> pageMenu(Page<MenuBean> page, LambdaQueryWrapper<MenuBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<MenuBean> listMenu(LambdaQueryWrapper<MenuBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public Boolean existMenu(LambdaQueryWrapper<MenuBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }
}