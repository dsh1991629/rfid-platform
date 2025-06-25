package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PageResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.MenuBean;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.MenuTreeDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.MenuService;
import com.rfid.platform.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            if (StringUtils.isBlank(menuDTO.getName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单名称不能为空");
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

    @PostMapping("/detail")
    public BaseResult<MenuDTO> menuDetail(@RequestBody MenuDTO menuDTO) {
        BaseResult<MenuDTO> result = new BaseResult<>();
        try {
            // 参数校验
            if (menuDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单ID不能为空");
                return result;
            }

            MenuBean menuBean = menuService.getMenuByPk(menuDTO.getId());
            if (menuBean != null) {
                MenuDTO ret = BeanUtil.copyProperties(menuBean, MenuDTO.class);

                // 格式化创建时间
                if (menuBean.getCreateTime() != null) {
                    menuDTO.setCreateDate(TimeUtil.getDateFormatterString(menuBean.getCreateTime()));
                }

                result.setData(menuDTO);
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("菜单不存在");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("查询菜单详情异常: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/page")
    public BaseResult<PageResult<MenuDTO>> pageMenu(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestBody MenuDTO menuDTO) {

        BaseResult<PageResult<MenuDTO>> result = new BaseResult<>();
        try {
            Page<MenuBean> page = new Page<>(pageNum, pageSize);
            // 构建查询条件
            LambdaQueryWrapper<MenuBean> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(menuDTO.getName())) {
                queryWrapper.like(MenuBean::getName, menuDTO.getName());
            }
            if (menuDTO.getId() != null) {
                queryWrapper.eq(MenuBean::getId, menuDTO.getId());
            }
            if (StringUtils.isNotBlank(menuDTO.getCode())) {
                queryWrapper.eq(MenuBean::getCode, menuDTO.getCode());
            }
            if (menuDTO.getParentId() != null) {
                queryWrapper.eq(MenuBean::getParentId, menuDTO.getParentId());
            }
            queryWrapper.orderByDesc(MenuBean::getCreateTime);


            IPage<MenuBean> pageResult = menuService.pageMenu(page, queryWrapper);

            // 转换为DTO
            List<MenuDTO> menuDTOList = pageResult.getRecords().stream().map(menuBean -> {
                MenuDTO ret = BeanUtil.copyProperties(menuBean, MenuDTO.class);
                if (menuBean.getCreateTime() != null) {
                    ret.setCreateDate(TimeUtil.getDateFormatterString(menuBean.getCreateTime()));
                }
                if (menuBean.getCreateId() != null) {
                    ret.setCreateAccountName(accountService.getAccountNameByPk(menuBean.getCreateId()));
                }
                return ret;
            }).collect(Collectors.toList());

            PageResult<MenuDTO> pageResultDTO = new PageResult<>();
            pageResultDTO.setPageNum(pageNum);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotal(pageResult.getTotal());
            pageResultDTO.setPages(pageResult.getPages());
            pageResultDTO.setData(menuDTOList);

            result.setData(pageResultDTO);
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("分页查询菜单异常: " + e.getMessage());
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
