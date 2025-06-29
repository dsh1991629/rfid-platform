package com.rfid.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rfid.platform.entity.AccountRoleRelBean;
import com.rfid.platform.entity.MenuBean;
import com.rfid.platform.entity.RoleBean;
import com.rfid.platform.entity.RoleMenuRelBean;
import com.rfid.platform.mapper.RoleMapper;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountRoleRelService;
import com.rfid.platform.service.MenuService;
import com.rfid.platform.service.RoleMenuRelService;
import com.rfid.platform.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleBean> implements RoleService {

    @Autowired
    @Lazy
    private AccountRoleRelService accountRoleRelService;

    @Autowired
    @Lazy
    private RoleMenuRelService roleMenuRelService;

    @Autowired
    @Lazy
    private MenuService menuService;


    @Transactional
    @Override
    public boolean saveRole(RoleBean entity, List<MenuDTO> menuDTOS) {
        super.save(entity);
        if (CollectionUtils.isNotEmpty(menuDTOS)) {
            // 收集所有需要保存的菜单ID（包括层级结构中的所有菜单）
            Set<Long> allMenuIds = collectAllMenuIds(menuDTOS);
            
            // 创建角色菜单关系
            List<RoleMenuRelBean> roleMenuRelBeans = allMenuIds.stream().map(menuId -> {
                RoleMenuRelBean roleMenuRelBean = new RoleMenuRelBean();
                roleMenuRelBean.setRoleId(entity.getId());
                roleMenuRelBean.setMenuId(menuId);
                return roleMenuRelBean;
            }).collect(Collectors.toUnmodifiableList());
            
            roleMenuRelService.saveRoleMenuRels(roleMenuRelBeans);
        }
        return true;
    }

    /**
     * 递归收集所有菜单ID，包括父菜单和子菜单
     * @param menuDTOS 菜单列表
     * @return 所有菜单ID的集合
     */
    private Set<Long> collectAllMenuIds(List<MenuDTO> menuDTOS) {
        Set<Long> allMenuIds = new HashSet<>();
        
        for (MenuDTO menuDTO : menuDTOS) {
            // 添加当前菜单ID
            if (menuDTO.getId() != null) {
                allMenuIds.add(menuDTO.getId());
            }
            
            // 如果有父菜单ID，也需要添加（确保父菜单权限）
            if (menuDTO.getParentId() != null) {
                addParentMenuIds(menuDTO.getParentId(), allMenuIds);
            }
            
            // 查找并添加所有子菜单ID
            addChildMenuIds(menuDTO.getId(), allMenuIds);
        }
        
        return allMenuIds;
    }

    /**
     * 递归添加父菜单ID
     * @param parentId 父菜单ID
     * @param allMenuIds 菜单ID集合
     */
    private void addParentMenuIds(Long parentId, Set<Long> allMenuIds) {
        if (parentId != null && !allMenuIds.contains(parentId)) {
            allMenuIds.add(parentId);
            
            // 查询父菜单信息
            MenuBean parentMenu = menuService.getMenuByPk(parentId);
            if (parentMenu != null && parentMenu.getParentId() != null) {
                // 递归添加上级父菜单
                addParentMenuIds(parentMenu.getParentId(), allMenuIds);
            }
        }
    }

    /**
     * 递归添加子菜单ID
     * @param menuId 当前菜单ID
     * @param allMenuIds 菜单ID集合
     */
    private void addChildMenuIds(Long menuId, Set<Long> allMenuIds) {
        if (menuId == null) return;
        
        // 查询所有子菜单
        LambdaQueryWrapper<MenuBean> childWrapper = Wrappers.lambdaQuery();
        childWrapper.eq(MenuBean::getParentId, menuId);
        List<MenuBean> childMenus = menuService.listMenu(childWrapper);
        
        for (MenuBean childMenu : childMenus) {
            if (!allMenuIds.contains(childMenu.getId())) {
                allMenuIds.add(childMenu.getId());
                // 递归添加子菜单的子菜单
                addChildMenuIds(childMenu.getId(), allMenuIds);
            }
        }
    }

    @Transactional
    @Override
    public boolean removeRoleByPk(Long id) {
        super.removeById(id);
        LambdaQueryWrapper<RoleMenuRelBean> delWrapper = Wrappers.lambdaQuery();
        delWrapper.eq(RoleMenuRelBean::getRoleId, id);
        roleMenuRelService.removeRoleMenuRels(delWrapper);
        return true;
    }

    @Transactional
    @Override
    public boolean updateRoleByPk(RoleBean entity, List<MenuDTO> menuDTOS) {
        super.updateById(entity);
    
        // 先删除原有的角色菜单关系
        LambdaQueryWrapper<RoleMenuRelBean> delWrapper = Wrappers.lambdaQuery();
        delWrapper.eq(RoleMenuRelBean::getRoleId, entity.getId());
        roleMenuRelService.removeRoleMenuRels(delWrapper);
    
        if (CollectionUtils.isNotEmpty(menuDTOS)) {
            // 收集所有需要保存的菜单ID（包括层级结构中的所有菜单）
            Set<Long> allMenuIds = collectAllMenuIds(menuDTOS);
            
            // 创建新的角色菜单关系
            List<RoleMenuRelBean> roleMenuRelBeans = allMenuIds.stream().map(menuId -> {
                RoleMenuRelBean roleMenuRelBean = new RoleMenuRelBean();
                roleMenuRelBean.setRoleId(entity.getId());
                roleMenuRelBean.setMenuId(menuId);
                return roleMenuRelBean;
            }).collect(Collectors.toUnmodifiableList());
            
            roleMenuRelService.saveRoleMenuRels(roleMenuRelBeans);
        }
        return true;
    }

    @Override
    public RoleBean getRoleByPk(Long id) {
        return super.getById(id);
    }

    @Override
    public IPage<RoleBean> pageRole(Page<RoleBean> page, LambdaQueryWrapper<RoleBean> query) {
        return super.page(page, query);
    }

    @Override
    public List<RoleBean> listRole(LambdaQueryWrapper<RoleBean> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public Boolean existRole(LambdaQueryWrapper<RoleBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }

    @Override
    public RoleDTO queryRoleByAccountId(Long accountId) {
        LambdaQueryWrapper<AccountRoleRelBean> roleRelWrapper = Wrappers.lambdaQuery();
        roleRelWrapper.eq(AccountRoleRelBean::getAccountId, accountId);
        List<AccountRoleRelBean> accountRoleRelBeans = accountRoleRelService.listAccountRoleRel(roleRelWrapper);
        if (CollectionUtils.isEmpty(accountRoleRelBeans)) {
            return null;
        }
        AccountRoleRelBean accountRoleRelBean = accountRoleRelBeans.get(0);
        Long roleId = accountRoleRelBean.getRoleId();
        RoleBean roleBean = super.getById(roleId);
        if (Objects.isNull(roleBean)) {
            return null;
        }
        RoleDTO roleDTO = BeanUtil.copyProperties(roleBean, RoleDTO.class);
        return roleDTO;
    }
}