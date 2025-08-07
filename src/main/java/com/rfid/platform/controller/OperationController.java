package com.rfid.platform.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.common.PageResult;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.SelectDTO;
import com.rfid.platform.persistence.StorageOrderDTO;
import com.rfid.platform.persistence.StorageOrderPageQueryDTO;
import com.rfid.platform.persistence.storage.InventoryOrderCreateRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderResponseDTO;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderService;
import com.rfid.platform.util.TimeUtil;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 操作管理控制器
 * 
 * @author RFID Platform Team
 * @version 1.0
 * @since 2024
 */
@Tag(name = "操作管理", description = "操作管理相关接口，包括盘点单创建，单号查询，推送操作")
@RestController
@RequestMapping(value = "/rfid/operation")
public class OperationController {

    @Autowired
    private TagStorageOrderService tagStorageOrderService;

    @Autowired
    private TagStorageOrderDetailService tagStorageOrderDetailService;



    @Operation(summary = "创建盘点通知单", description = "RMS系统自建盘点通知单")
    @PostMapping(value = "/create-inventoryorder")
    public RfidApiResponseDTO<InventoryOrderResponseDTO> createInventoryOrder(
            @Parameter(description = "盘点通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<InventoryOrderCreateRequestDTO> requestDTO) {
        RfidApiResponseDTO<InventoryOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("盘点通知单数据不存在");
            return response;
        }

        String orderNoRms = tagStorageOrderService.createInventoryTagStorageOrder(requestDTO.getTimeStamp(), requestDTO.getData());
        InventoryOrderResponseDTO inventoryOrderResponseDTO = new InventoryOrderResponseDTO();
        inventoryOrderResponseDTO.setOrderNoRMS(orderNoRms);
        response.setData(inventoryOrderResponseDTO);
        return response;
    }


    @Operation(summary = "查询通知单类型值", description = "查询通知单类型值")
    @PostMapping(value = "/storage-order/types")
    public RfidApiResponseDTO<List<SelectDTO>> storageOrderTypes() {
        RfidApiResponseDTO<List<SelectDTO>> response = RfidApiResponseDTO.success();
        List<SelectDTO> selectDTOS = new ArrayList<>();
        SelectDTO inBoundDTO = new SelectDTO();
        inBoundDTO.setId("1");
        inBoundDTO.setName("入库单");
        selectDTOS.add(inBoundDTO);

        SelectDTO outBoundDTO = new SelectDTO();
        outBoundDTO.setId("2");
        outBoundDTO.setName("出库单");
        selectDTOS.add(outBoundDTO);

        SelectDTO inventoryDTO = new SelectDTO();
        inventoryDTO.setId("3");
        inventoryDTO.setName("盘点单");
        selectDTOS.add(inventoryDTO);

        response.setData(selectDTOS);
        return response;
    }


    @Operation(summary = "查询通知单状态值", description = "查询通知单状态值")
    @PostMapping(value = "/storage-order/states")
    public RfidApiResponseDTO<List<SelectDTO>> storageOrderStates() {
        RfidApiResponseDTO<List<SelectDTO>> response = RfidApiResponseDTO.success();

        List<SelectDTO> selectDTOS = new ArrayList<>();
        SelectDTO sendDTO = new SelectDTO();
        sendDTO.setId("1");
        sendDTO.setName("下发");
        selectDTOS.add(sendDTO);

        SelectDTO doingDTO = new SelectDTO();
        doingDTO.setId("2");
        doingDTO.setName("设备盘点中");
        selectDTOS.add(doingDTO);

        SelectDTO doneDTO = new SelectDTO();
        doneDTO.setId("3");
        doneDTO.setName("设备盘点完成");
        selectDTOS.add(doneDTO);

        SelectDTO syncDTO = new SelectDTO();
        syncDTO.setId("4");
        syncDTO.setName("推送WMS完成");
        selectDTOS.add(doneDTO);

        SelectDTO cancelDTO = new SelectDTO();
        cancelDTO.setId("5");
        cancelDTO.setName("取消");
        selectDTOS.add(cancelDTO);

        response.setData(selectDTOS);
        return response;
    }


    @Operation(
            summary = "分页查询通知单",
            description = "根据查询条件分页获取通知单列表，支持按类型，状态，时间条件进行筛选查询。"
    )
    @PostMapping(value = "/storage-order/page")
    public RfidApiResponseDTO<PageResult<StorageOrderDTO>> storageOrderPage(
            @Parameter(description = "通知单分页查询条件", required = true)
            @RequestBody RfidApiRequestDTO<StorageOrderPageQueryDTO> requestDTO,
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        RfidApiResponseDTO<PageResult<StorageOrderDTO>> result = RfidApiResponseDTO.success();
        try {
            Page<TagStorageOrderBean> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<TagStorageOrderBean> queryWrapper = new LambdaQueryWrapper<>();

            if (Objects.nonNull(requestDTO.getData())) {
                StorageOrderPageQueryDTO storageOrderPageQueryDTO = requestDTO.getData();
                // 构建查询条件
                if (StringUtils.isNotBlank(storageOrderPageQueryDTO.getOrderNoRms())) {
                    queryWrapper.like(TagStorageOrderBean::getOrderNoRms, storageOrderPageQueryDTO.getOrderNoRms());
                }
                if (Objects.nonNull(storageOrderPageQueryDTO.getType())) {
                    queryWrapper.eq(TagStorageOrderBean::getType, storageOrderPageQueryDTO.getType());
                }
                if (Objects.nonNull(storageOrderPageQueryDTO.getState())) {
                    queryWrapper.eq(TagStorageOrderBean::getState, storageOrderPageQueryDTO.getState());
                }
                if (StringUtils.isNotBlank(storageOrderPageQueryDTO.getStartDate())) {
                    LocalDateTime startTime = TimeUtil.parseDayFormatterString(storageOrderPageQueryDTO.getStartDate());
                    queryWrapper.ge(TagStorageOrderBean::getCreateTime, startTime);
                }
                if (StringUtils.isNotBlank(storageOrderPageQueryDTO.getEndDate())) {
                    LocalDateTime endTime = TimeUtil.parseDayFormatterString(storageOrderPageQueryDTO.getEndDate());
                    queryWrapper.le(TagStorageOrderBean::getCreateTime, endTime);
                }
            }

            queryWrapper.orderByDesc(TagStorageOrderBean::getCreateTime);

            IPage<TagStorageOrderBean> pageResult = tagStorageOrderService.pageTagStorageOrder(page, queryWrapper);

            // 转换结果
            PageResult<StorageOrderDTO> pageResultDTO = new PageResult<>();
            pageResultDTO.setPageNum(pageNum);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setTotal(pageResult.getTotal());
            pageResultDTO.setPages(pageResult.getPages());

            List<StorageOrderDTO> dtoList = pageResult.getRecords().stream()
                    .map(bean -> {
                        StorageOrderDTO dto = BeanUtil.copyProperties(bean, StorageOrderDTO.class);
                        String typeName = convertTypeName(dto.getType());
                        dto.setTypeName(typeName);
                        String stateName = convertStateName(dto.getState());
                        dto.setStateName(stateName);

                        List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(dto.getOrderNoRms());
                        if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                            Integer totalQuantity = tagStorageOrderDetailBeans.stream()
                                    .mapToInt(detailBean -> detailBean.getQuantity() != null ? detailBean.getQuantity() : 0)
                                    .sum();
                            dto.setQuantity(totalQuantity);
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

    private String convertStateName(Integer state) {
        String stateName = "";
        switch (state) {
            case 1:
                stateName = "下发";
                break;
            case 2:
                stateName = "设备盘点中";
                break;
            case 3:
                stateName = "设备盘点完成";
                break;
            case 4:
                stateName = "推送WMS完成";
                break;
            case 5:
                stateName = "取消";
                break;
            default:
                stateName = "";
                break;
        }
        return stateName;
    }

    private String convertTypeName(Integer type) {
        String typeName = "";
        switch (type) {
            case 1:
                typeName = "入库单";
                break;
            case 2:
                typeName = "出库单";
                break;
            case 3:
                typeName = "盘点单";
                break;
            default:
                typeName = "";
                break;
        }
        return typeName;
    }

}
