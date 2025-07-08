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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        // 递归删除所有子菜单
        removeChildMenusRecursively(id);
        // 删除当前菜单
        return super.removeById(id);
    }
    
    /**
     * 递归删除指定菜单的所有子菜单
     * @param parentId 父菜单ID
     */
    private void removeChildMenusRecursively(Long parentId) {
        // 查询所有子菜单
        LambdaQueryWrapper<MenuBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MenuBean::getParentId, parentId);
        List<MenuBean> childMenus = super.list(queryWrapper);
        
        if (CollectionUtils.isNotEmpty(childMenus)) {
            for (MenuBean childMenu : childMenus) {
                // 递归删除子菜单的子菜单
                removeChildMenusRecursively(childMenu.getId());
                // 删除当前子菜单
                super.removeById(childMenu.getId());
            }
        }
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
        
        // 获取所有菜单并转换为DTO
        List<MenuDTO> allMenus = roleMenuRelBeans.stream().map(e -> {
            Long menuId = e.getMenuId();
            MenuBean menuBean = super.getById(menuId);
            MenuDTO menuDTO = BeanUtil.copyProperties(menuBean, MenuDTO.class);
            menuDTO.setChildren(new ArrayList<>()); // 初始化children列表
            return menuDTO;
        }).collect(Collectors.toList());
        
        // 按照层级组装菜单树
        return buildMenuTree(allMenus);
    }

    /**
     * 构建菜单树结构
     * @param menuList 菜单列表
     * @return 树形结构的菜单列表
     */
    private List<MenuDTO> buildMenuTree(List<MenuDTO> menuList) {
        // 创建id到菜单的映射
        Map<Long, MenuDTO> menuMap = menuList.stream()
                .collect(Collectors.toMap(MenuDTO::getId, menu -> menu));
        
        List<MenuDTO> rootMenus = new ArrayList<>();
        
        for (MenuDTO menu : menuList) {
            Long parentId = menu.getParentId();
            
            if (parentId == null || parentId == 0) {
                // 根菜单（没有父菜单）
                rootMenus.add(menu);
            } else {
                // 子菜单，添加到父菜单的children中
                MenuDTO parentMenu = menuMap.get(parentId);
                if (parentMenu != null) {
                    parentMenu.getChildren().add(menu);
                }
            }
        }
        
        // 对菜单进行排序（根据priority字段）
        sortMenusByPriority(rootMenus);
        
        return rootMenus;
    }

    /**
     * 递归排序菜单（根据priority字段）
     * @param menus 菜单列表
     */
    private void sortMenusByPriority(List<MenuDTO> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return;
        }
        
        // 按priority字段升序排序
        menus.sort((m1, m2) -> {
            Integer p1 = m1.getPriority() != null ? m1.getPriority() : Integer.MAX_VALUE;
            Integer p2 = m2.getPriority() != null ? m2.getPriority() : Integer.MAX_VALUE;
            return p1.compareTo(p2);
        });
        
        // 递归排序子菜单
        for (MenuDTO menu : menus) {
            sortMenusByPriority(menu.getChildren());
        }
    }


    @Override
    public List<MenuDTO> queryAdminMenus() {
        LambdaQueryWrapper<MenuBean> queryWrapper = Wrappers.lambdaQuery();
        List<MenuBean> menuBeans = super.list(queryWrapper);
        // 获取所有菜单并转换为DTO
        List<MenuDTO> allMenus = menuBeans.stream().map(e -> {
            MenuDTO menuDTO = BeanUtil.copyProperties(e, MenuDTO.class);
            menuDTO.setChildren(new ArrayList<>()); // 初始化children列表
            return menuDTO;
        }).collect(Collectors.toList());

        // 按照层级组装菜单树
        return buildMenuTree(allMenus);
    }
}