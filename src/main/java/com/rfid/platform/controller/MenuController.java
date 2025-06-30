package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.MenuBean;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.MenuTreeDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.MenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/rfid/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private AccountService accountService;


    @PostMapping("/create")
    public BaseResult<Long> createMenu(@RequestBody MenuDTO menuDTO) {
        BaseResult<Long> result = new BaseResult<>();
        try {
            // 参数校验
            if (StringUtils.isBlank(menuDTO.getName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单名称不能为空");
                return result;
            }

            // 检查菜单名称是否已存在
            LambdaQueryWrapper<MenuBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(MenuBean::getName, menuDTO.getName());
            Boolean existingMenus = menuService.existMenu(nameCheckWrapper);

            if (existingMenus) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单名称已存在，不能重复");
                return result;
            }

            MenuBean menuBean = BeanUtil.copyProperties(menuDTO, MenuBean.class);

            boolean success = menuService.saveMenu(menuBean);
            if (success) {
                result.setData(menuBean.getId());
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("创建菜单失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("创建菜单异常: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/delete")
    public BaseResult<Boolean> deleteMenu(@RequestBody MenuDTO menuDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (menuDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单ID不能为空");
                return result;
            }

            boolean success = menuService.removeMenuByPk(menuDTO.getId());
            result.setData(success);
            if (!success) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("删除菜单失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("删除菜单异常: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/update")
    public BaseResult<Boolean> updateMenu(@RequestBody MenuDTO menuDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (menuDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单ID不能为空");
                return result;
            }

            // 检查菜单名称是否已存在（排除当前菜单）
            LambdaQueryWrapper<MenuBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(MenuBean::getName, menuDTO.getName()).ne(MenuBean::getId, menuDTO.getId());
            Boolean existMenus = menuService.existMenu(nameCheckWrapper);

            if (existMenus) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单名称已存在，不能重复");
                return result;
            }

            MenuBean menuBean = BeanUtil.copyProperties(menuDTO, MenuBean.class);

            boolean success = menuService.updateMenuByPk(menuBean);
            result.setData(success);
            if (!success) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("更新菜单失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("更新菜单异常: " + e.getMessage());
        }
        return result;
    }


    @PostMapping("/tree")
    public BaseResult<List<MenuTreeDTO>> menuTree() {
        BaseResult<List<MenuTreeDTO>> result = new BaseResult<>();
        try {
            // 查询所有菜单
            LambdaQueryWrapper<MenuBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(MenuBean::getParentId, MenuBean::getId);
            List<MenuBean> allMenus = menuService.listMenu(queryWrapper);

            // 转换为TreeDTO并构建树形结构
            List<MenuTreeDTO> menuTreeList = buildMenuTree(allMenus, null);
            result.setData(menuTreeList);
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("查询菜单树异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 构建菜单树
     */
    private List<MenuTreeDTO> buildMenuTree(List<MenuBean> allMenus, Long parentId) {
        List<MenuTreeDTO> treeList = new ArrayList<>();

        for (MenuBean menu : allMenus) {
            if ((parentId == null && menu.getParentId() == null) ||
                    (parentId != null && parentId.equals(menu.getParentId()))) {

                MenuTreeDTO treeDTO = new MenuTreeDTO();
                treeDTO.setId(menu.getId());
                treeDTO.setName(menu.getName());

                // 递归查找子菜单
                List<MenuTreeDTO> children = buildMenuTree(allMenus, menu.getId());
                treeDTO.setChildren(children);

                treeList.add(treeDTO);
            }
        }

        return treeList;
    }

}
