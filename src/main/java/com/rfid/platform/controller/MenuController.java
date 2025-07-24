package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.MenuBean;
import com.rfid.platform.persistence.MenuCreateDTO;
import com.rfid.platform.persistence.MenuDeleteDTO;
import com.rfid.platform.persistence.MenuTreeDTO;
import com.rfid.platform.persistence.MenuUpdateDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单管理控制器
 * 提供菜单的增删改查功能
 */
@Tag(name = "菜单管理", description = "菜单管理相关接口")
@RestController
@RequestMapping(value = "/rfid/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;



    /**
     * 创建菜单
     * @param requestDTO 菜单创建参数
     * @return 创建结果，包含菜单ID
     */
    @Operation(summary = "创建菜单", description = "创建新的菜单项")
    @PostMapping("/create")
    public RfidApiResponseDTO<Long> createMenu(
            @Parameter(description = "菜单创建参数", required = true)
            @RequestBody RfidApiRequestDTO<MenuCreateDTO> requestDTO) {
        RfidApiResponseDTO<Long> result = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            result.setStatus(false);
            result.setMessage("请求数据不能为空");
            return result;
        }
        try {
            MenuCreateDTO menuCreateDTO = requestDTO.getData();
            // 参数校验
            if (StringUtils.isBlank(menuCreateDTO.getName())) {
                result.setStatus(false);
                result.setMessage("菜单名称不能为空");
                return result;
            }

            // 检查菜单名称是否已存在
            LambdaQueryWrapper<MenuBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(MenuBean::getName, menuCreateDTO.getName());
            Boolean existingMenus = menuService.existMenu(nameCheckWrapper);

            if (existingMenus) {
                result.setStatus(false);
                result.setMessage("菜单名称已存在，不能重复");
                return result;
            }

            MenuBean menuBean = BeanUtil.copyProperties(menuCreateDTO, MenuBean.class);

            boolean success = menuService.saveMenu(menuBean);
            if (success) {
                result.setData(menuBean.getId());
            } else {
                result.setStatus(false);
                result.setMessage("创建菜单失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("创建菜单异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除菜单
     * @param requestDTO 菜单删除参数
     * @return 删除结果
     */
    @Operation(summary = "删除菜单", description = "根据菜单ID删除菜单")
    @PostMapping("/delete")
    public RfidApiResponseDTO<Boolean> deleteMenu(
            @Parameter(description = "菜单删除参数", required = true)
            @RequestBody RfidApiRequestDTO<MenuDeleteDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            result.setStatus(false);
            result.setMessage("请求数据不能为空");
            return result;
        }
        try {
            MenuDeleteDTO menuDeleteDTO = requestDTO.getData();
            // 参数校验
            if (menuDeleteDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("菜单ID不能为空");
                return result;
            }

            boolean success = menuService.removeMenuByPk(menuDeleteDTO.getId());
            result.setData(success);
            if (!success) {
                result.setStatus(false);
                result.setMessage("删除菜单失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("删除菜单异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新菜单
     * @param requestDTO 菜单更新参数
     * @return 更新结果
     */
    @Operation(summary = "更新菜单", description = "根据菜单ID更新菜单信息")
    @PostMapping("/update")
    public RfidApiResponseDTO<Boolean> updateMenu(
            @Parameter(description = "菜单更新参数", required = true)
            @RequestBody RfidApiRequestDTO<MenuUpdateDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            result.setStatus(false);
            result.setMessage("请求数据不能为空");
            return result;
        }
        try {
            MenuUpdateDTO menuUpdateDTO = requestDTO.getData();
            // 参数校验
            if (menuUpdateDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("菜单ID不能为空");
                return result;
            }

            // 检查菜单名称是否已存在（排除当前菜单）
            LambdaQueryWrapper<MenuBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(MenuBean::getName, menuUpdateDTO.getName()).ne(MenuBean::getId, menuUpdateDTO.getId());
            Boolean existMenus = menuService.existMenu(nameCheckWrapper);

            if (existMenus) {
                result.setStatus(false);
                result.setMessage("菜单名称已存在，不能重复");
                return result;
            }

            MenuBean menuBean = BeanUtil.copyProperties(menuUpdateDTO, MenuBean.class);

            boolean success = menuService.updateMenuByPk(menuBean);
            result.setData(success);
            if (!success) {
                result.setStatus(false);
                result.setMessage("更新菜单失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("更新菜单异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取菜单树
     * @return 菜单树结构
     */
    @Operation(summary = "获取菜单树", description = "获取完整的菜单树形结构")
    @PostMapping("/tree")
    public RfidApiResponseDTO<List<MenuTreeDTO>> menuTree() {
        RfidApiResponseDTO<List<MenuTreeDTO>> result = RfidApiResponseDTO.success();
        try {
            // 查询所有菜单
            LambdaQueryWrapper<MenuBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(MenuBean::getParentId, MenuBean::getId);
            List<MenuBean> allMenus = menuService.listMenu(queryWrapper);

            // 转换为TreeDTO并构建树形结构
            List<MenuTreeDTO> menuTreeList = buildMenuTree(allMenus, null);
            result.setData(menuTreeList);
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("查询菜单树异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 构建菜单树
     * 递归构建菜单的树形结构
     * @param allMenus 所有菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树列表
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
