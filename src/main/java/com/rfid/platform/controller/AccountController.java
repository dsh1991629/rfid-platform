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
import com.rfid.platform.entity.AccountDepartmentRelBean;
import com.rfid.platform.persistence.AccountCreateDTO;
import com.rfid.platform.persistence.AccountDTO;
import com.rfid.platform.persistence.AccountDeleteDTO;
import com.rfid.platform.persistence.AccountDepartmentQueryDTO;
import com.rfid.platform.persistence.AccountPageQueryDTO;
import com.rfid.platform.persistence.AccountUpdateDTO;
import com.rfid.platform.persistence.DepartmentDTO;
import com.rfid.platform.persistence.MenuDTO;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountDepartRelService;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DepartmentService;
import com.rfid.platform.service.MenuService;
import com.rfid.platform.service.RoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private MenuService menuService;


    @PostMapping(value = "/create")
    public BaseResult<Long> addAccount(@RequestBody AccountCreateDTO accountCreateDTO) {
        BaseResult<Long> result = new BaseResult<>();
        try {

            // 参数校验
            if (StringUtils.isBlank(accountCreateDTO.getCode())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户编码不能为空");
                return result;
            }

            if (StringUtils.isBlank(accountCreateDTO.getName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户名称不能为空");
                return result;
            }


            // 检查角色名称是否已存在
            LambdaQueryWrapper<AccountBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(AccountBean::getCode, accountCreateDTO.getCode());
            Boolean existingAccounts = accountService.existAccount(nameCheckWrapper);

            if (existingAccounts) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户已存在，不能重复");
                return result;
            }


            AccountBean accountBean = BeanUtil.copyProperties(accountCreateDTO, AccountBean.class);
            boolean success = accountService.saveAccount(accountBean, accountCreateDTO.getDepartment(), accountCreateDTO.getRole());
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
    public BaseResult<Boolean> deleteAccount(@RequestBody AccountDeleteDTO accountDeleteDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            if (accountDeleteDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户ID不能为空");
                result.setData(false);
                return result;
            }
            boolean success = accountService.removeAccountByPk(accountDeleteDTO.getId());
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
    public BaseResult<Boolean> updateAccount(@RequestBody AccountUpdateDTO accountUpdateDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            if (accountUpdateDTO.getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户ID不能为空");
                result.setData(false);
                return result;
            }

            // 检查账号编码是否已存在（排除当前编码）
            LambdaQueryWrapper<AccountBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(AccountBean::getName, accountUpdateDTO.getCode()).ne(AccountBean::getId, accountUpdateDTO.getId());
            Boolean existAccount = accountService.existAccount(nameCheckWrapper);

            if (existAccount) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("账户编码已存在，不能重复");
                return result;
            }

            AccountBean accountBean = BeanUtil.copyProperties(accountUpdateDTO, AccountBean.class);
            boolean success = accountService.updateAccountByPk(accountBean, accountUpdateDTO.getDepartment(), accountUpdateDTO.getRole());
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


    @PostMapping(value = "/page")
    public BaseResult<PageResult<AccountDTO>> accountPage(@RequestBody AccountPageQueryDTO accountPageQueryDTO,
                                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        BaseResult<PageResult<AccountDTO>> result = new BaseResult<>();
        try {
            Page<AccountBean> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();

            // 构建查询条件
            if (StringUtils.isNotBlank(accountPageQueryDTO.getCode())) {
                queryWrapper.like(AccountBean::getCode, accountPageQueryDTO.getCode());
            }
            if (StringUtils.isNotBlank(accountPageQueryDTO.getName())) {
                queryWrapper.like(AccountBean::getName, accountPageQueryDTO.getName());
            }

            Long departmentId = null;
            if (Objects.nonNull(accountPageQueryDTO.getDepartment()) && Objects.nonNull(accountPageQueryDTO.getDepartment().getId())) {
                departmentId = accountPageQueryDTO.getDepartment().getId();
            }

            Long roleId = null;
            if (Objects.nonNull(accountPageQueryDTO.getRole()) && Objects.nonNull(accountPageQueryDTO.getRole().getId())) {
                roleId = accountPageQueryDTO.getRole().getId();
            }

            IPage<AccountBean> pageResult = accountService.pageAccount(page, queryWrapper, departmentId, roleId);

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

                        RoleDTO roleDTO = roleService.queryRoleByAccountId(bean.getId());
                        if (Objects.nonNull(roleDTO)) {
                            List<MenuDTO> menuDTOS = menuService.queryMenusByRole(roleDTO.getId());
                            roleDTO.setMenus(menuDTOS);
                            dto.setRole(roleDTO);
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
    public BaseResult<List<AccountDTO>> accountByDepartment(@RequestBody AccountDepartmentQueryDTO accountDepartmentQueryDTO) {
        BaseResult<List<AccountDTO>> result = new BaseResult<>();
        try {
            if (accountDepartmentQueryDTO.getDepartment() == null || accountDepartmentQueryDTO.getDepartment().getId() == null) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("部门ID不能为空");
                return result;
            }

            LambdaQueryWrapper<AccountDepartmentRelBean> departRelWrapper = Wrappers.lambdaQuery();
            departRelWrapper.eq(AccountDepartmentRelBean::getDepartmentId, accountDepartmentQueryDTO.getDepartment().getId());
            List<AccountDepartmentRelBean> accountDepartmentRelBeans = accountDepartRelService.listAccountDepartRel(departRelWrapper);

            if (CollectionUtils.isEmpty(accountDepartmentRelBeans)) {
                result.setData(List.of());
                result.setMessage("查询成功");
                return result;
            }

            List<AccountDTO> accountDTOS = accountDepartmentRelBeans.stream().map(e -> {
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

                RoleDTO roleDTO = roleService.queryRoleByAccountId(e.getId());
                if (Objects.nonNull(roleDTO)) {
                    List<MenuDTO> menuDTOS = menuService.queryMenusByRole(roleDTO.getId());
                    roleDTO.setMenus(menuDTOS);
                    resultDTO.setRole(roleDTO);
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
