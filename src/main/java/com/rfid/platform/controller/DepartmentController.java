package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rfid.platform.entity.DepartmentBean;
import com.rfid.platform.persistence.DepartmentCreateDTO;
import com.rfid.platform.persistence.DepartmentDeleteDTO;
import com.rfid.platform.persistence.DepartmentTreeDTO;
import com.rfid.platform.persistence.DepartmentUpdateDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DepartmentService;
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
 * 部门管理控制器
 * 提供部门的增删改查和树形结构查询功能
 */
@Tag(name = "部门管理", description = "部门管理相关接口")
@RestController
@RequestMapping(value = "/rfid/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private AccountService accountService;

    /**
     * 创建部门
     * @param requestDTO 部门创建请求参数
     * @return 创建结果，包含新创建部门的ID
     */
    @Operation(summary = "创建部门", description = "创建新的部门，部门名称不能重复")
    @PostMapping(value = "/create")
    public RfidApiResponseDTO<Long> createDepartment(
            @Parameter(description = "部门创建参数", required = true)
            @RequestBody RfidApiRequestDTO<DepartmentCreateDTO> requestDTO) {
        RfidApiResponseDTO<Long> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("部门数据不能为空");
                return result;
            }

            DepartmentCreateDTO departmentCreateDTO = requestDTO.getData();
            // 参数校验
            if (StringUtils.isBlank(departmentCreateDTO.getName())) {
                result.setStatus(false);
                result.setMessage("部门名称不能为空");
                return result;
            }

            // 检查部门名称是否已存在
            LambdaQueryWrapper<DepartmentBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(DepartmentBean::getName, departmentCreateDTO.getName());
            Boolean existingDepartments = departmentService.existDepartment(nameCheckWrapper);

            if (existingDepartments) {
                result.setStatus(false);
                result.setMessage("部门名称已存在，不能重复");
                return result;
            }
            
            // DTO转Bean
            DepartmentBean departmentBean = BeanUtil.copyProperties(departmentCreateDTO, DepartmentBean.class);
            
            // 保存部门
            boolean success = departmentService.saveDepartment(departmentBean);
            if (success) {
                result.setData(departmentBean.getId());
                result.setMessage("创建成功");
            } else {
                result.setStatus(false);
                result.setMessage("创建失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除部门
     * @param requestDTO 部门删除请求参数
     * @return 删除结果，会级联删除所有子部门
     */
    @Operation(summary = "删除部门", description = "删除指定部门，会级联删除所有子部门")
    @PostMapping(value = "/delete")
    public RfidApiResponseDTO<Boolean> deleteDepartment(
            @Parameter(description = "部门删除参数", required = true)
            @RequestBody RfidApiRequestDTO<DepartmentDeleteDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("部门数据不能为空");
                return result;
            }

            DepartmentDeleteDTO departmentDeleteDTO = requestDTO.getData();
            // 参数校验
            if (departmentDeleteDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("部门ID不能为空");
                return result;
            }
            
            // 检查部门是否存在
            DepartmentBean existingDepartment = departmentService.getDepartmentByPk(departmentDeleteDTO.getId());
            if (existingDepartment == null) {
                result.setStatus(false);
                result.setMessage("部门不存在");
                return result;
            }
            
            // 级联删除部门及其所有子部门
            boolean success = departmentService.removeDepartmentCascade(departmentDeleteDTO.getId());
            result.setData(success);
            if (success) {
                result.setMessage("删除成功（包含所有子部门）");
            } else {
                result.setStatus(false);
                result.setMessage("删除失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 更新部门信息
     * @param requestDTO 部门更新请求参数
     * @return 更新结果
     */
    @Operation(summary = "更新部门", description = "更新部门信息，部门名称不能与其他部门重复")
    @PostMapping(value = "/update")
    public RfidApiResponseDTO<Boolean> updateDepartment(
            @Parameter(description = "部门更新参数", required = true)
            @RequestBody RfidApiRequestDTO<DepartmentUpdateDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("部门数据不能为空");
                return result;
            }

            DepartmentUpdateDTO departmentUpdateDTO = requestDTO.getData();
            // 参数校验
            if (departmentUpdateDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("部门ID不能为空");
                return result;
            }

            // 检查部门名称是否已存在（排除当前部门）
            LambdaQueryWrapper<DepartmentBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(DepartmentBean::getName, departmentUpdateDTO.getName()).ne(DepartmentBean::getId, departmentUpdateDTO.getId());
            Boolean existingDepartments = departmentService.existDepartment(nameCheckWrapper);

            if (existingDepartments) {
                result.setStatus(false);
                result.setMessage("部门名称已存在，不能重复");
                return result;
            }
            
            // DTO转Bean
            DepartmentBean departmentBean = BeanUtil.copyProperties(departmentUpdateDTO, DepartmentBean.class);
            
            // 更新部门
            boolean success = departmentService.updateDepartmentByPk(departmentBean);
            result.setData(success);
            if (success) {
                result.setMessage("更新成功");
            } else {
                result.setStatus(false);
                result.setMessage("更新失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
            result.setData(false);
        }
        return result;
    }

    /**
     * 获取部门树形结构
     * @return 部门树形结构数据
     */
    @Operation(summary = "获取部门树", description = "获取所有部门的树形结构")
    @PostMapping(value = "/tree")
    public RfidApiResponseDTO<DepartmentTreeDTO> departmentTree() {
        RfidApiResponseDTO<DepartmentTreeDTO> result = RfidApiResponseDTO.success();
        try {
            // 查询所有部门
            LambdaQueryWrapper<DepartmentBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(DepartmentBean::getCreateTime);
            List<DepartmentBean> allDepartments = departmentService.listDepartment(queryWrapper);
            
            // 构建树形结构
            DepartmentTreeDTO treeRoot = buildDepartmentTree(allDepartments);
            
            if (treeRoot != null) {
                result.setData(treeRoot);
                result.setStatus(false);
                result.setMessage("查询成功");
            } else {
                result.setStatus(false);
                result.setMessage("未找到根部门");
            }

        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 构建部门树形结构
     * @param allDepartments 所有部门列表
     * @return 树形结构根节点
     */
    private DepartmentTreeDTO buildDepartmentTree(List<DepartmentBean> allDepartments) {
        // 找到根节点（parentId为null的节点）
        DepartmentBean rootDepartment = null;
        for (DepartmentBean department : allDepartments) {
            if (department.getParentId() == null) {
                rootDepartment = department;
                break;
            }
        }
        
        if (rootDepartment == null) {
            return null; // 没有找到根节点
        }
        
        // 构建根节点
        DepartmentTreeDTO rootNode = new DepartmentTreeDTO();
        rootNode.setId(rootDepartment.getId());
        rootNode.setName(rootDepartment.getName());
        // 递归构建子节点
        rootNode.setChildren(buildChildrenTree(allDepartments, rootDepartment.getId()));
        
        return rootNode;
    }
    
    /**
     * 递归构建子部门树形结构
     * @param allDepartments 所有部门列表
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    private List<DepartmentTreeDTO> buildChildrenTree(List<DepartmentBean> allDepartments, Long parentId) {
        List<DepartmentTreeDTO> children = new ArrayList<>();

        for (DepartmentBean department : allDepartments) {
            if (parentId.equals(department.getParentId())) {
                DepartmentTreeDTO childNode = new DepartmentTreeDTO();
                childNode.setId(department.getId());
                childNode.setName(department.getName());
                // 递归查找子部门
                childNode.setChildren(buildChildrenTree(allDepartments, department.getId()));
                children.add(childNode);
            }
        }
        return children;
    }

}