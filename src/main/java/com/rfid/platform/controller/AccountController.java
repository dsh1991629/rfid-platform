package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PageResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.entity.AccountDepartRelBean;
import com.rfid.platform.persistence.AccountDTO;
import com.rfid.platform.persistence.DepartmentDTO;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountDepartRelService;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DepartmentService;
import com.rfid.platform.service.RoleService;
import org.apache.commons.collections4.CollectionUtils;
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
@RequestMapping(value = "/rfid/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AccountDepartRelService accountDepartRelService;


    @PostMapping(value = "/create")
    public BaseResult<Long> addAccount(@RequestBody AccountDTO accountDTO) {
        BaseResult<Long> result = new BaseResult<>();
        try {

            // 参数校验
            if (StringUtils.isBlank(accountDTO.getCode())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户编码不能为空");
                return result;
            }

            if (StringUtils.isBlank(accountDTO.getName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户名称不能为空");
                return result;
            }


            // 检查角色名称是否已存在
            LambdaQueryWrapper<AccountBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(AccountBean::getCode, accountDTO.getCode());
            Boolean existingAccounts = accountService.existAccount(nameCheckWrapper);

            if (existingAccounts) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户已存在，不能重复");
                return result;
            }


            AccountBean accountBean = BeanUtil.copyProperties(accountDTO, AccountBean.class);
            Long departmentId = accountDTO.getDepartment().getId();
            List<Long> roleIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(accountDTO.getRoles())) {
                roleIds.addAll(accountDTO.getRoles().stream().map(RoleDTO::getId).collect(Collectors.toUnmodifiableList()));
            }

            boolean success = accountService.saveAccount(accountBean, departmentId, roleIds);
            if (success) {
                result.setData(accountBean.getId());
                result.setMessage("账户创建成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户创建失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("账户创建异常: " + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/delete")
    public BaseResult<Boolean> deleteAccount(@RequestBody AccountDTO accountDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            if (accountDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户ID不能为空");
                result.setData(false);
                return result;
            }
            boolean success = accountService.removeAccountByPk(accountDTO.getId());
            result.setData(success);
            if (success) {
                result.setMessage("账户删除成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户删除失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("账户删除异常: " + e.getMessage());
            result.setData(false);
        }
        return result;
    }

    @PostMapping(value = "/update")
    public BaseResult<Boolean> updateAccount(@RequestBody AccountDTO accountDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            if (accountDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户ID不能为空");
                result.setData(false);
                return result;
            }

            // 检查账号编码是否已存在（排除当前编码）
            LambdaQueryWrapper<AccountBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(AccountBean::getName, accountDTO.getCode()).ne(AccountBean::getId, accountDTO.getId());
            Boolean existAccount = accountService.existAccount(nameCheckWrapper);

            if (existAccount) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户编码已存在，不能重复");
                return result;
            }

            AccountBean accountBean = BeanUtil.copyProperties(accountDTO, AccountBean.class);
            Long departmentId = accountDTO.getDepartment().getId();
            List<Long> roleIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(accountDTO.getRoles())) {
                roleIds.addAll(accountDTO.getRoles().stream().map(RoleDTO::getId).collect(Collectors.toUnmodifiableList()));
            }

            boolean success = accountService.updateAccountByPk(accountBean, departmentId, roleIds);
            result.setData(success);
            if (success) {
                result.setMessage("账户更新成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户更新失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("账户更新异常: " + e.getMessage());
            result.setData(false);
        }
        return result;
    }

    @PostMapping(value = "/detail")
    public BaseResult<AccountDTO> accountDetail(@RequestBody AccountDTO accountDTO) {
        BaseResult<AccountDTO> result = new BaseResult<>();
        try {
            if (accountDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户ID不能为空");
                return result;
            }
            AccountBean accountBean = accountService.getAccountByPk(accountDTO.getId());
            if (accountBean != null) {
                AccountDTO resultDTO = BeanUtil.copyProperties(accountBean, AccountDTO.class);
                // 根据状态设置状态名称
                if (resultDTO.getState() != null) {
                    resultDTO.setStateName(resultDTO.getState() == 1 ? "正常" : "禁用");
                }

                DepartmentDTO departmentDTO = departmentService.queryDepartmentByAccountId(accountBean.getId());
                if (Objects.nonNull(departmentDTO)) {
                    resultDTO.setDepartment(departmentDTO);
                }

                List<RoleDTO> roleDTOS = roleService.listRolesByAccountId(accountBean.getId());
                if (CollectionUtils.isNotEmpty(roleDTOS)) {
                    resultDTO.setRoles(roleDTOS);
                }

                result.setData(resultDTO);
                result.setMessage("查询成功");
            } else {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户不存在");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("查询异常: " + e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/page")
    public BaseResult<PageResult<AccountDTO>> accountPage(@RequestBody AccountDTO accountDTO,
                                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        BaseResult<PageResult<AccountDTO>> result = new BaseResult<>();
        try {
            Page<AccountBean> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();

            // 构建查询条件
            if (StringUtils.isNotBlank(accountDTO.getCode())) {
                queryWrapper.like(AccountBean::getCode, accountDTO.getCode());
            }
            if (StringUtils.isNotBlank(accountDTO.getName())) {
                queryWrapper.like(AccountBean::getName, accountDTO.getName());
            }
            if (StringUtils.isNotBlank(accountDTO.getEmail())) {
                queryWrapper.like(AccountBean::getEmail, accountDTO.getEmail());
            }
            if (StringUtils.isNotBlank(accountDTO.getPhone())) {
                queryWrapper.like(AccountBean::getPhone, accountDTO.getPhone());
            }
            if (accountDTO.getState() != null) {
                queryWrapper.eq(AccountBean::getState, accountDTO.getState());
            }

            IPage<AccountBean> pageResult = accountService.pageAccount(page, queryWrapper);

            // 转换结果
            PageResult<AccountDTO> pageResultDTO = new PageResult<>();
            pageResultDTO.setPageNum(pageNum);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotal(pageResult.getTotal());
            pageResultDTO.setPages(pageResult.getPages());

            List<AccountDTO> dtoList = pageResult.getRecords().stream()
                    .map(bean -> {
                        AccountDTO dto = BeanUtil.copyProperties(bean, AccountDTO.class);
                        // 设置状态名称
                        if (dto.getState() != null) {
                            dto.setStateName(dto.getState() == 1 ? "正常" : "禁用");
                        }

                        DepartmentDTO departmentDTO = departmentService.queryDepartmentByAccountId(bean.getId());
                        if (Objects.nonNull(departmentDTO)) {
                            dto.setDepartment(departmentDTO);
                        }

                        List<RoleDTO> roleDTOS = roleService.listRolesByAccountId(bean.getId());
                        if (CollectionUtils.isNotEmpty(roleDTOS)) {
                            dto.setRoles(roleDTOS);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            pageResultDTO.setData(dtoList);
            result.setData(pageResultDTO);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("分页查询异常: " + e.getMessage());
        }
        return result;
    }


    @PostMapping(value = "/list/department")
    public BaseResult<List<AccountDTO>> accountByDepartment(@RequestBody AccountDTO accountDTO) {
        BaseResult<List<AccountDTO>> result = new BaseResult<>();
        try {
            if (accountDTO.getDepartment() == null || accountDTO.getDepartment().getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门ID不能为空");
                return result;
            }

            LambdaQueryWrapper<AccountDepartRelBean> departRelWrapper = Wrappers.lambdaQuery();
            departRelWrapper.eq(AccountDepartRelBean::getDepartmentId, accountDTO.getDepartment().getId());
            List<AccountDepartRelBean> accountDepartRelBeans = accountDepartRelService.listAccountDepartRel(departRelWrapper);

            if (CollectionUtils.isEmpty(accountDepartRelBeans)) {
                result.setData(List.of());
                result.setMessage("查询成功");
                return result;
            }

            List<AccountDTO> accountDTOS = accountDepartRelBeans.stream().map(e -> {
                AccountBean accountBean = accountService.getAccountByPk(e.getId());

                AccountDTO resultDTO = BeanUtil.copyProperties(accountBean, AccountDTO.class);
                // 根据状态设置状态名称
                if (resultDTO.getState() != null) {
                    resultDTO.setStateName(resultDTO.getState() == 1 ? "正常" : "禁用");
                }

                DepartmentDTO departmentDTO = departmentService.queryDepartmentByAccountId(e.getId());
                if (Objects.nonNull(departmentDTO)) {
                    resultDTO.setDepartment(departmentDTO);
                }

                List<RoleDTO> roleDTOS = roleService.listRolesByAccountId(e.getId());
                if (CollectionUtils.isNotEmpty(roleDTOS)) {
                    resultDTO.setRoles(roleDTOS);
                }
                return resultDTO;
            }).collect(Collectors.toUnmodifiableList());

            result.setData(accountDTOS);
            result.setMessage("查询成功");
            return result;
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("查询异常: " + e.getMessage());
        }
        return result;
    }


}
