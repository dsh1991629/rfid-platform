package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.common.BaseResult;
import com.rfid.platform.common.PageResult;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.DeviceInfoBean;
import com.rfid.platform.persistence.DeviceAccountRelDeleteDTO;
import com.rfid.platform.persistence.DeviceAccountRelQueryDTO;
import com.rfid.platform.persistence.DeviceAccountRelUpdateDTO;
import com.rfid.platform.persistence.DeviceAccountRepeatDTO;
import com.rfid.platform.persistence.DeviceCreateDTO;
import com.rfid.platform.persistence.DeviceDTO;
import com.rfid.platform.persistence.DeviceDeleteDTO;
import com.rfid.platform.persistence.DevicePageQueryDTO;
import com.rfid.platform.persistence.DeviceUpdateDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.DeviceAccountRelService;
import com.rfid.platform.service.DeviceInfoService;
import com.rfid.platform.util.TimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.collections4.CollectionUtils;
import com.rfid.platform.entity.DeviceAccountRelBean;
import com.rfid.platform.entity.AccountBean;

@Tag(name = "设备管理", description = "设备管理相关接口")
@RestController
@RequestMapping(value = "/rfid/device")
public class DeviceController {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DeviceAccountRelService deviceAccountRelService;


    @Operation(summary = "创建设备", description = "创建新的设备，设备编码不能重复")
    @PostMapping(value = "/create")
    public BaseResult<Long> createDevice(@Parameter(description = "设备创建参数", required = true)
                                         @RequestBody DeviceCreateDTO deviceCreateDTO) {
        BaseResult<Long> result = new BaseResult<>();
        try {
            // 参数校验
            if (StringUtils.isBlank(deviceCreateDTO.getDeviceName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备名称不能为空");
                return result;
            }

            if (StringUtils.isBlank(deviceCreateDTO.getDeviceModel())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备类型不能为空");
                return result;
            }

            if (StringUtils.isBlank(deviceCreateDTO.getDeviceCode())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备编码不能为空");
                return result;
            }

            // 检查部门名称是否已存在
            LambdaQueryWrapper<DeviceInfoBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(DeviceInfoBean::getDeviceCode, deviceCreateDTO.getDeviceCode());
            Boolean existingDepartments = deviceInfoService.existDevice(nameCheckWrapper);

            if (existingDepartments) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备编码已存在，不能重复");
                return result;
            }

            // DTO转Bean
            DeviceInfoBean deviceInfoBean = BeanUtil.copyProperties(deviceCreateDTO, DeviceInfoBean.class);

            // 保存设备
            boolean success = deviceInfoService.saveDevice(deviceInfoBean);
            if (success) {
                result.setData(deviceInfoBean.getId());
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


    @Operation(summary = "更新设备", description = "更新设备")
    @PostMapping(value = "/update")
    public BaseResult<Boolean> updateDevice(@Parameter(description = "设备更新参数", required = true)
                                            @RequestBody DeviceUpdateDTO deviceUpdateDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (Objects.isNull(deviceUpdateDTO.getId())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备ID不能为空");
                return result;
            }

            // 参数校验
            if (StringUtils.isBlank(deviceUpdateDTO.getDeviceName())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备名称不能为空");
                return result;
            }

            if (StringUtils.isBlank(deviceUpdateDTO.getDeviceModel())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备类型不能为空");
                return result;
            }

            if (StringUtils.isBlank(deviceUpdateDTO.getDeviceCode())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备编码不能为空");
                return result;
            }

            // 检查部门名称是否已存在
            LambdaQueryWrapper<DeviceInfoBean> nameCheckWrapper = new LambdaQueryWrapper<>();
            nameCheckWrapper.eq(DeviceInfoBean::getDeviceCode, deviceUpdateDTO.getDeviceCode());
            nameCheckWrapper.ne(DeviceInfoBean::getId, deviceUpdateDTO.getId());
            Boolean existingDepartments = deviceInfoService.existDevice(nameCheckWrapper);

            if (existingDepartments) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备编码已存在，不能重复");
                return result;
            }

            DeviceInfoBean deviceInfoBean = BeanUtil.copyProperties(deviceUpdateDTO, DeviceInfoBean.class);
            // 保存设备
            boolean success = deviceInfoService.updateDevice(deviceInfoBean);
            if (success) {
                result.setData(true);
                result.setMessage("更新成功");
            } else {
                result.setData(false);
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("更新失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }

        return result;
    }


    @Operation(summary = "删除设备", description = "删除设备")
    @PostMapping(value = "/delete")
    public BaseResult<Boolean> deleteDevice(@Parameter(description = "设备删除参数", required = true)
                                            @RequestBody DeviceDeleteDTO deviceDeleteDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (Objects.isNull(deviceDeleteDTO.getId())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备ID不能为空");
                return result;
            }

            // 保存设备
            boolean success = deviceInfoService.deleteDevice(deviceDeleteDTO.getId());
            if (success) {
                result.setData(true);
                result.setMessage("删除成功");
            } else {
                result.setData(false);
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("删除失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("系统异常：" + e.getMessage());
        }

        return result;
    }


    @Operation(
            summary = "分页查询设备",
            description = "根据查询条件分页获取设备列表，支持按设备编码、类型、名称等条件进行筛选查询。"
    )
    @PostMapping(value = "/page")
    public BaseResult<PageResult<DeviceDTO>> accountPage(
            @Parameter(description = "设备分页查询条件", required = true)
            @RequestBody DevicePageQueryDTO devicePageQueryDTO,
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        BaseResult<PageResult<DeviceDTO>> result = new BaseResult<>();
        try {
            Page<DeviceInfoBean> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<DeviceInfoBean> queryWrapper = new LambdaQueryWrapper<>();

            // 构建查询条件
            if (StringUtils.isNotBlank(devicePageQueryDTO.getDeviceCode())) {
                queryWrapper.like(DeviceInfoBean::getDeviceCode, devicePageQueryDTO.getDeviceCode());
            }
            if (StringUtils.isNotBlank(devicePageQueryDTO.getDeviceName())) {
                queryWrapper.like(DeviceInfoBean::getDeviceName, devicePageQueryDTO.getDeviceName());
            }

            if (StringUtils.isNotBlank(devicePageQueryDTO.getDeviceModel())) {
                queryWrapper.like(DeviceInfoBean::getDeviceModel, devicePageQueryDTO.getDeviceModel());
            }
            queryWrapper.orderByDesc(DeviceInfoBean::getCreateTime);

            IPage<DeviceInfoBean> pageResult = deviceInfoService.pageDevice(page, queryWrapper);

            // 转换结果
            PageResult<DeviceDTO> pageResultDTO = new PageResult<>();
            pageResultDTO.setPageNum(pageNum);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotal(pageResult.getTotal());
            pageResultDTO.setPages(pageResult.getPages());

            List<DeviceDTO> dtoList = pageResult.getRecords().stream()
                    .map(bean -> {
                        DeviceDTO dto = BeanUtil.copyProperties(bean, DeviceDTO.class);
                        String accountNameByPk = accountService.getAccountNameByPk(bean.getCreateId());
                        dto.setCreateUser(accountNameByPk);
                        dto.setCreateDate(TimeUtil.getDateFormatterString(bean.getCreateTime()));
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


    @Operation(summary = "查询设备关联的账户", description = "根据设备ID查询关联的账户列表")
    @PostMapping(value = "/account/rel/list")
    public BaseResult<List<DeviceAccountRepeatDTO>> deviceRelatedAccountQuery(@Parameter(description = "设备账户关联查询参数", required =
            true)
                                                                              @RequestBody DeviceAccountRelQueryDTO deviceAccountRelQueryDTO) {
        BaseResult<List<DeviceAccountRepeatDTO>> result = new BaseResult<>();
        try {
            // 参数校验
            if (Objects.isNull(deviceAccountRelQueryDTO.getId())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备ID不能为空");
                return result;
            }

            // 通过设备id查询设备账户关联的集合
            List<DeviceAccountRelBean> deviceAccountRelList = deviceAccountRelService.listDeviceAccountRel(deviceAccountRelQueryDTO.getId());

            if (CollectionUtils.isEmpty(deviceAccountRelList)) {
                result.setData(List.of());
                result.setMessage("查询成功");
                return result;
            }

            // 提取账户ID列表
            List<DeviceAccountRepeatDTO> accounts = deviceAccountRelList.stream()
                    .map(e -> {
                        DeviceAccountRepeatDTO deviceAccountRepeatDTO = new DeviceAccountRepeatDTO();
                        AccountBean accountBean = accountService.getAccountByPk(e.getAccountId());
                        if (Objects.nonNull(accountBean)) {
                            deviceAccountRepeatDTO.setAccountName(accountBean.getName());
                            deviceAccountRepeatDTO.setAccountCode(accountBean.getCode());
                        }
                        deviceAccountRepeatDTO.setAccountId(e.getAccountId());
                        deviceAccountRepeatDTO.setRepeatTimes(e.getRepeatTimes());
                        return deviceAccountRepeatDTO;
                    })
                    .collect(Collectors.toUnmodifiableList());


            result.setData(accounts);
            result.setMessage("查询成功");
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("查询异常: " + e.getMessage());
        }
        return result;
    }


    @Operation(summary = "删除设备关联的账户", description = "删除设备ID查询关联的账户列表")
    @PostMapping(value = "/account/rel/delete")
    public BaseResult<Boolean> deviceRelatedAccountDelete(@Parameter(description = "设备账户关联删除参数", required = true)
                                                          @RequestBody DeviceAccountRelDeleteDTO deviceAccountRelDeleteDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (Objects.isNull(deviceAccountRelDeleteDTO.getId())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备ID不能为空");
                return result;
            }


            if (CollectionUtils.isEmpty(deviceAccountRelDeleteDTO.getAccountIds())) {
                result.setData(false);
                result.setMessage("账户为空");
                return result;
            }

            boolean success = deviceAccountRelService.deleteDeviceAccountRelWithAccount(deviceAccountRelDeleteDTO.getId(), deviceAccountRelDeleteDTO.getAccountIds());
            if (success) {
                result.setData(true);
                result.setMessage("删除成功");
            } else {
                result.setData(false);
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("删除失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("查询异常: " + e.getMessage());
        }
        return result;
    }

    @Operation(summary = "更新设备关联的账户", description = "更新设备ID查询关联的账户列表")
    @PostMapping(value = "/account/rel/update")
    public BaseResult<Boolean> deviceRelatedAccountUpdate(@Parameter(description = "设备账户关联更新参数", required = true)
                                                          @RequestBody DeviceAccountRelUpdateDTO deviceAccountRelUpdateDTO) {
        BaseResult<Boolean> result = new BaseResult<>();
        try {
            // 参数校验
            if (Objects.isNull(deviceAccountRelUpdateDTO.getId())) {
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("设备ID不能为空");
                return result;
            }


            if (CollectionUtils.isEmpty(deviceAccountRelUpdateDTO.getDeviceAccounts())) {
                result.setData(false);
                result.setMessage("账户为空");
                return result;
            }

            boolean success = deviceAccountRelService.updateDeviceAccountRel(deviceAccountRelUpdateDTO.getId(),
                    deviceAccountRelUpdateDTO.getDeviceAccounts());
            if (success) {
                result.setData(true);
                result.setMessage("更新成功");
            } else {
                result.setData(false);
                result.setCode(PlatformConstant.RET_CODE.FAILED);
                result.setMessage("更新失败");
            }
        } catch (Exception e) {
            result.setCode(PlatformConstant.RET_CODE.FAILED);
            result.setMessage("查询异常: " + e.getMessage());
        }
        return result;
    }

}
