package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PageResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.DepartmentBean;
import com.rfid.platform.persistence.DepartmentDTO;
import com.rfid.platform.persistence.DepartmentTreeDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DepartmentService;
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
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rfid/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private AccountService accountService;

    @PostMapping(value = "/create")
    public BaseResult<Long> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        BaseResult<Long> result = new BaseResult<>();
        try {
            // 参数校验
            if (StringUtils.isBlank(departmentDTO.getName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门名称不能为空");
                return result;
            }

            // 检查部门名称是否已存在
            LambdaQueryWrapper<DepartmentBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(DepartmentBean::getName, departmentDTO.getName());
            Boolean existingDepartments = departmentService.existDepartment(nameCheckWrapper);

            if (existingDepartments) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门名称已存在，不能重复");
                return result;
            }
            
            // DTO转Bean
            DepartmentBean departmentBean = BeanUtil.copyProperties(departmentDTO, DepartmentBean.class);
            
            // 保存部门
            boolean success = departmentService.saveDepartment(departmentBean);
            if (success) {
                result.setData(departmentBean.getId());
                result.setMessage("创建成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("创建失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/delete")
    public BaseResult<Boolean> deleteDepartment(@RequestBody DepartmentDTO departmentDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (departmentDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门ID不能为空");
                return result;
            }
            
            // 删除部门
            boolean success = departmentService.removeDepartmentByPk(departmentDTO.getId());
            result.setData(success);
            if (success) {
                result.setMessage("删除成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("删除失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/update")
    public BaseResult<Boolean> updateDepartment(@RequestBody DepartmentDTO departmentDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (departmentDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门ID不能为空");
                return result;
            }

            // 检查部门名称是否已存在（排除当前部门）
            LambdaQueryWrapper<DepartmentBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(DepartmentBean::getName, departmentDTO.getName()).ne(DepartmentBean::getId, departmentDTO.getId());
            Boolean existingDepartments = departmentService.existDepartment(nameCheckWrapper);

            if (existingDepartments) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门名称已存在，不能重复");
                return result;
            }
            
            // DTO转Bean
            DepartmentBean departmentBean = BeanUtil.copyProperties(departmentDTO, DepartmentBean.class);
            
            // 更新部门
            boolean success = departmentService.updateDepartmentByPk(departmentBean);
            result.setData(success);
            if (success) {
                result.setMessage("更新成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("更新失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
            result.setData(false);
        }
        return result;
    }

    @PostMapping(value = "/detail")
    public BaseResult<DepartmentDTO> queryDepartmentDetail(@RequestBody DepartmentDTO departmentDTO) {
        BaseResult<DepartmentDTO> result = new BaseResult<>();
        try {
            // 参数校验
            if (departmentDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门ID不能为空");
                return result;
            }
            
            // 查询部门详情
            DepartmentBean departmentBean = departmentService.getDepartmentByPk(departmentDTO.getId());
            if (departmentBean != null) {
                DepartmentDTO resultDTO = BeanUtil.copyProperties(departmentBean, DepartmentDTO.class);
                // 格式化创建时间
                if (departmentBean.getCreateTime() != null) {
                    resultDTO.setCreateDate(TimeUtil.getDateFormatterString(departmentBean.getCreateTime()));
                }
                if (Objects.nonNull(departmentBean.getCreateId())) {
                    resultDTO.setCreateAccountName(accountService.getAccountNameByPk(departmentBean.getCreateId()));
                }
                result.setData(resultDTO);
                result.setMessage("查询成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门不存在");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/page")
    public BaseResult<PageResult<DepartmentDTO>> queryDepartmentPage(@RequestBody DepartmentDTO departmentDTO,
                                                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        BaseResult<PageResult<DepartmentDTO>> result = new BaseResult<>();
        try {
            // 构建分页对象
            Page<DepartmentBean> page = new Page<>(pageNum, pageSize);
            
            // 构建查询条件
            LambdaQueryWrapper<DepartmentBean> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(departmentDTO.getName())) {
                queryWrapper.like(DepartmentBean::getName, departmentDTO.getName());
            }
            if (departmentDTO.getId() != null) {
                queryWrapper.eq(DepartmentBean::getId, departmentDTO.getId());
            }
            if (departmentDTO.getParentId() != null) {
                queryWrapper.eq(DepartmentBean::getParentId, departmentDTO.getParentId());
            }
            queryWrapper.orderByDesc(DepartmentBean::getCreateTime);
            
            // 执行分页查询
            IPage<DepartmentBean> pageResult = departmentService.pageDepartment(page, queryWrapper);
            
            // 转换结果
            PageResult<DepartmentDTO> pageResultDTO = new PageResult<>();
            pageResultDTO.setPageNum(pageNum);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotal(pageResult.getTotal());
            pageResultDTO.setPages(pageResult.getPages());
            
            List<DepartmentDTO> departmentDTOList = pageResult.getRecords().stream().map(departmentBean -> {
                DepartmentDTO dto = BeanUtil.copyProperties(departmentBean, DepartmentDTO.class);
                // 格式化创建时间
                if (departmentBean.getCreateTime() != null) {
                    dto.setCreateDate(TimeUtil.getDateFormatterString(departmentBean.getCreateTime()));
                }
                if (Objects.nonNull(departmentBean.getCreateId())) {
                    dto.setCreateAccountName(accountService.getAccountNameByPk(departmentBean.getCreateId()));
                }
                return dto;
            }).collect(Collectors.toUnmodifiableList());
            
            pageResultDTO.setData(departmentDTOList);
            result.setData(pageResultDTO);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/list")
    public BaseResult<List<DepartmentDTO>> listDepartment(@RequestBody DepartmentDTO departmentDTO) {
        BaseResult<List<DepartmentDTO>> result = new BaseResult<>();
        try {
            // 查询所有部门
            LambdaQueryWrapper<DepartmentBean> queryWrapper = new LambdaQueryWrapper<>();
            if (Objects.nonNull(departmentDTO.getId())) {
                queryWrapper.eq(DepartmentBean::getId, departmentDTO.getId());
            }
            if (StringUtils.isNotBlank(departmentDTO.getName())) {
                queryWrapper.like(DepartmentBean::getName, departmentDTO.getName());
            }
            if (Objects.nonNull(departmentDTO.getParentId())) {
                queryWrapper.eq(DepartmentBean::getParentId, departmentDTO.getParentId());
            }
            queryWrapper.orderByAsc(DepartmentBean::getCreateTime);
            List<DepartmentBean> departmentBeanList = departmentService.listDepartment(queryWrapper);
            
            // 转换为DepartmentDTO
            List<DepartmentDTO> departmentDTOList = departmentBeanList.stream().map(departmentBean -> {
                DepartmentDTO ret = new DepartmentDTO();
                ret.setId(departmentBean.getId());
                ret.setName(departmentBean.getName());
                ret.setParentId(departmentBean.getParentId());
                // 格式化创建时间
                if (departmentBean.getCreateTime() != null) {
                    ret.setCreateDate(TimeUtil.getDateFormatterString(departmentBean.getCreateTime()));
                }
                if (Objects.nonNull(departmentBean.getCreateId())) {
                    ret.setCreateAccountName(accountService.getAccountNameByPk(departmentBean.getCreateId()));
                }
                return ret;
            }).collect(Collectors.toUnmodifiableList());
            
            result.setData(departmentDTOList);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/tree")
    public BaseResult<DepartmentTreeDTO> departmentTree() {
        BaseResult<DepartmentTreeDTO> result = new BaseResult<>();
        try {
            // 查询所有部门
            LambdaQueryWrapper<DepartmentBean> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(DepartmentBean::getCreateTime);
            List<DepartmentBean> allDepartments = departmentService.listDepartment(queryWrapper);
            
            // 构建树形结构
            DepartmentTreeDTO treeRoot = buildDepartmentTree(allDepartments);
            
            if (treeRoot != null) {
                result.setData(treeRoot);
                result.setCode(PlatformConstant.RET_CODE.SUCCESS);
                result.setMessage("查询成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("未找到根部门");
            }

        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
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