package com.rfid.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.MenuBean;
import com.rfid.platform.entity.RoleMenuRelBean;
import com.rfid.platform.mapper.MenuMapper;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.service.RoleMenuRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuBean> implements MenuService {

    @Autowired
    @Lazy
    private RoleMenuRelService roleMenuRelService;


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

    @Override
    public List<MenuDTO> queryMenusByRole(Long roleId) {
        LambdaQueryWrapper<RoleMenuRelBean> menuRelWrapper = Wrappers.lambdaQuery();
        menuRelWrapper.eq(RoleMenuRelBean::getRoleId, roleId);
        List<RoleMenuRelBean> roleMenuRelBeans = roleMenuRelService.listRoleMenuRels(menuRelWrapper);
        if (CollectionUtils.isEmpty(roleMenuRelBeans)) {
            return List.of();
        }
        List<MenuDTO> menuDTOS = roleMenuRelBeans.stream().map(e -> {
            Long menuId = e.getMenuId();
            MenuBean menuBean = super.getById(menuId);
            MenuDTO menuDTO = BeanUtil.copyProperties(menuBean, MenuDTO.class);
            return menuDTO;
        }).collect(Collectors.toUnmodifiableList());
        return menuDTOS;
    }
}