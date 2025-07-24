package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.common.PageResult;
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
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.RoleDTO;
import com.rfid.platform.service.AccountDepartRelService;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DepartmentService;
import com.rfid.platform.service.MenuService;
import com.rfid.platform.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * 账户管理控制器
 * 提供账户的增删改查等基本操作功能
 * 
 * @author RFID Platform Team
 * @version 1.0
 * @since 2024
 */
@Tag(name = "账户管理", description = "账户管理相关接口，包括账户的创建、删除、更新、查询等功能")
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

    /**
     * 创建新账户
     * 根据提供的账户信息创建新的用户账户，包括账户编码、名称、部门和角色等信息
     * 
     * @param requestDTO 账户创建数据传输对象，包含创建账户所需的所有信息
     * @return 返回创建结果，成功时包含新创建账户的ID
     */
    @Operation(
        summary = "创建账户",
        description = "创建新的用户账户，需要提供账户编码、名称、部门和角色等基本信息。账户编码不能重复。"
    )
    @PostMapping(value = "/create")
    public RfidApiResponseDTO<Long> addAccount(
            @Parameter(description = "账户创建信息", required = true)
        @RequestBody RfidApiRequestDTO<AccountCreateDTO> requestDTO) {
        RfidApiResponseDTO<Long> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("账户数据不能为空");
                return result;
            }

            AccountCreateDTO accountCreateDTO = requestDTO.getData();
            // 参数校验
            if (StringUtils.isBlank(accountCreateDTO.getCode())) {
                result.setStatus(false);
                result.setMessage("账户编码不能为空");
                return result;
            }

            if (StringUtils.isBlank(accountCreateDTO.getName())) {
                result.setStatus(false);
                result.setMessage("账户名称不能为空");
                return result;
            }

            // 检查角色名称是否已存在
            LambdaQueryWrapper<AccountBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(AccountBean::getCode, accountCreateDTO.getCode());
            Boolean existingAccounts = accountService.existAccount(nameCheckWrapper);

            if (existingAccounts) {
                result.setStatus(false);
                result.setMessage("账户已存在，不能重复");
                return result;
            }

            AccountBean accountBean = BeanUtil.copyProperties(accountCreateDTO, AccountBean.class);
            boolean success = accountService.saveAccount(accountBean, accountCreateDTO.getDepartment(), accountCreateDTO.getRole());
            if (success) {
                result.setData(accountBean.getId());
                result.setMessage("账户创建成功");
            } else {
                result.setStatus(false);
                result.setMessage("账户创建失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("账户创建异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 删除账户
     * 根据账户ID删除指定的用户账户
     * 
     * @param requestDTO 账户删除数据传输对象，包含要删除的账户ID
     * @return 返回删除操作的结果
     */
    @Operation(
        summary = "删除账户",
        description = "根据账户ID删除指定的用户账户，删除操作不可逆，请谨慎操作。"
    )
    @PostMapping(value = "/delete")
    public RfidApiResponseDTO<Boolean> deleteAccount(
        @Parameter(description = "账户删除信息", required = true)
        @RequestBody RfidApiRequestDTO<AccountDeleteDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("账户数据不能为空");
                return result;
            }

            AccountDeleteDTO accountDeleteDTO = requestDTO.getData();
            if (accountDeleteDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("账户ID不能为空");
                result.setData(false);
                return result;
            }
            boolean success = accountService.removeAccountByPk(accountDeleteDTO.getId());
            result.setData(success);
            if (success) {
                result.setMessage("账户删除成功");
            } else {
                result.setStatus(false);
                result.setMessage("账户删除失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("账户删除异常: " + e.getMessage());
            result.setData(false);
        }
        return result;
    }

    /**
     * 更新账户信息
     * 根据账户ID更新账户的基本信息，包括名称、部门、角色等
     * 
     * @param requestDTO 账户更新数据传输对象，包含要更新的账户信息
     * @return 返回更新操作的结果
     */
    @Operation(
        summary = "更新账户",
        description = "根据账户ID更新账户的基本信息，包括账户名称、部门、角色等信息。账户编码不能与其他账户重复。"
    )
    @PostMapping(value = "/update")
    public RfidApiResponseDTO<Boolean> updateAccount(
        @Parameter(description = "账户更新信息", required = true)
        @RequestBody RfidApiRequestDTO<AccountUpdateDTO> requestDTO) {
        RfidApiResponseDTO<Boolean> result = RfidApiResponseDTO.success();
        try {
            if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
                result.setStatus(false);
                result.setMessage("账户数据不能为空");
                return result;
            }

            AccountUpdateDTO accountUpdateDTO = requestDTO.getData();
            if (accountUpdateDTO.getId() == null) {
                result.setStatus(false);
                result.setMessage("账户ID不能为空");
                result.setData(false);
                return result;
            }

            // 检查账号编码是否已存在（排除当前编码）
            LambdaQueryWrapper<AccountBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(AccountBean::getName, accountUpdateDTO.getCode()).ne(AccountBean::getId, accountUpdateDTO.getId());
            Boolean existAccount = accountService.existAccount(nameCheckWrapper);

            if (existAccount) {
                result.setStatus(false);
                result.setMessage("账户编码已存在，不能重复");
                return result;
            }

            AccountBean accountBean = BeanUtil.copyProperties(accountUpdateDTO, AccountBean.class);
            boolean success = accountService.updateAccountByPk(accountBean, accountUpdateDTO.getDepartment(), accountUpdateDTO.getRole());
            result.setData(success);
            if (success) {
                result.setMessage("账户更新成功");
            } else {
                result.setStatus(false);
                result.setMessage("账户更新失败");
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("账户更新异常: " + e.getMessage());
            result.setData(false);
        }
        return result;
    }

    /**
     * 分页查询账户列表
     * 根据查询条件分页获取账户列表，支持按账户编码、名称、部门、角色等条件进行筛选
     * 
     * @param requestDTO 账户分页查询条件
     * @param pageNum 页码，默认为1
     * @param pageSize 每页大小，默认为10
     * @return 返回分页查询结果，包含账户列表和分页信息
     */
    @Operation(
        summary = "分页查询账户",
        description = "根据查询条件分页获取账户列表，支持按账户编码、名称、部门、角色等条件进行筛选查询。"
    )
    @PostMapping(value = "/page")
    public RfidApiResponseDTO<PageResult<AccountDTO>> accountPage(
        @Parameter(description = "账户分页查询条件", required = true)
        @RequestBody RfidApiRequestDTO<AccountPageQueryDTO> requestDTO,
        @Parameter(description = "页码，从1开始", example = "1")
        @RequestParam(defaultValue = "1") Integer pageNum,
        @Parameter(description = "每页大小", example = "10")
        @RequestParam(defaultValue = "10") Integer pageSize) {
        RfidApiResponseDTO<PageResult<AccountDTO>> result = RfidApiResponseDTO.success();
        try {
            Page<AccountBean> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<AccountBean> queryWrapper = new LambdaQueryWrapper<>();

            Long departmentId = null;
            Long roleId = null;
            if (Objects.nonNull(requestDTO.getData())) {
                AccountPageQueryDTO accountPageQueryDTO = requestDTO.getData();
                // 构建查询条件
                if (StringUtils.isNotBlank(accountPageQueryDTO.getCode())) {
                    queryWrapper.like(AccountBean::getCode, accountPageQueryDTO.getCode());
                }
                if (StringUtils.isNotBlank(accountPageQueryDTO.getName())) {
                    queryWrapper.like(AccountBean::getName, accountPageQueryDTO.getName());
                }

                if (Objects.nonNull(accountPageQueryDTO.getDepartment()) && Objects.nonNull(accountPageQueryDTO.getDepartment().getId())) {
                    departmentId = accountPageQueryDTO.getDepartment().getId();
                }

                if (Objects.nonNull(accountPageQueryDTO.getRole()) && Objects.nonNull(accountPageQueryDTO.getRole().getId())) {
                    roleId = accountPageQueryDTO.getRole().getId();
                }
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
            result.setStatus(false);
            result.setMessage("分页查询异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据部门查询账户列表
     * 获取指定部门下的所有账户信息，包括账户的基本信息、角色和权限等
     * 
     * @param requestDTO 部门账户查询条件，包含部门ID
     * @return 返回指定部门下的账户列表
     */
    @Operation(
        summary = "根据部门查询账户",
        description = "获取指定部门下的所有账户信息，包括账户的基本信息、所属角色和相关权限菜单。"
    )
    @PostMapping(value = "/list/department")
    public RfidApiResponseDTO<List<AccountDTO>> accountByDepartment(
        @Parameter(description = "部门账户查询条件", required = true)
        @RequestBody RfidApiRequestDTO<AccountDepartmentQueryDTO> requestDTO) {
        RfidApiResponseDTO<List<AccountDTO>> result = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            result.setStatus(false);
            result.setMessage("账户数据不能为空");
            return result;
        }

        AccountDepartmentQueryDTO accountDepartmentQueryDTO = requestDTO.getData();
        try {
            if (accountDepartmentQueryDTO.getDepartment() == null || accountDepartmentQueryDTO.getDepartment().getId() == null) {
                result.setStatus(false);
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
            result.setStatus(false);
            result.setMessage("查询异常: " + e.getMessage());
        }
        return result;
    }
}
