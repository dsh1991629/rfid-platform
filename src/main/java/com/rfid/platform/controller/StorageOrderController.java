package com.rfid.platform.controller;

import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.CancelStorageOrderResponseDTO;
import com.rfid.platform.persistence.storage.CancelStorageOrderRequestDTO;
import com.rfid.platform.persistence.storage.StorageOrderRequestDTO;
import com.rfid.platform.persistence.storage.StorageOrderResponseDTO;
import com.rfid.platform.service.TagStorageOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "WMS通知单下发，取消", description = "RFID通知单相关接口，包括入库、出库、盘点通知单的创建和取消")
@RestController
@RequestMapping(value = "/rfid")
public class StorageOrderController {

    @Autowired
    private TagStorageOrderService tagStorageOrderService;

    @Operation(summary = "发送入库通知单", description = "创建并发送入库通知单到RFID系统")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "入库通知单创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/send-inboundorder")
    public RfidApiResponseDTO<StorageOrderResponseDTO> sendInBoundOrder(
            @Parameter(description = "入库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<StorageOrderRequestDTO> requestDTO){
        RfidApiResponseDTO<StorageOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("入库通知单不存在");
            return response;
        }

        StorageOrderRequestDTO storageOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(storageOrderRequestDTO.getOrderNo())) {
            response.setStatus(false);
            response.setMessage("入库通知单号不存在");
            return response;
        }

        Long id = tagStorageOrderService.saveInboundTagStorageOrder(requestDTO.getTimeStamp(), storageOrderRequestDTO.getOrderNo(), storageOrderRequestDTO.getItems());
        StorageOrderResponseDTO storageOrderResponseDTO = new StorageOrderResponseDTO();
        storageOrderResponseDTO.setOrderNo(storageOrderResponseDTO.getOrderNo());
        storageOrderResponseDTO.setId(id);
        response.setData(storageOrderResponseDTO);
        return response;
    }

    @Operation(summary = "取消入库通知单", description = "取消已创建的入库通知单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "入库通知单取消成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或订单不能被取消"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/cancel-inboundorder")
    public RfidApiResponseDTO<CancelStorageOrderResponseDTO> cancelInBoundOrder(
            @Parameter(description = "取消入库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<CancelStorageOrderRequestDTO> requestDTO){
        RfidApiResponseDTO<CancelStorageOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("入库通知单不存在");
            return response;
        }

        CancelStorageOrderRequestDTO cancelStorageOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(cancelStorageOrderRequestDTO.getOrderNo())) {
            response.setStatus(false);
            response.setMessage("入库通知单号不存在");
            return response;
        }

        String orderNo = cancelStorageOrderRequestDTO.getOrderNo();
        boolean canCancel = tagStorageOrderService.checkStorageOrderCancelable(orderNo, PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);
        if (!canCancel) {
            response.setStatus(false);
            response.setMessage("入库通知单不能被取消");
            return response;
        }

        Long id = tagStorageOrderService.cancelTagStorageOrder(requestDTO.getTimeStamp(), orderNo, PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND);

        CancelStorageOrderResponseDTO storageOrderResponseDTO = new CancelStorageOrderResponseDTO();
        storageOrderResponseDTO.setOrderNo(orderNo);
        storageOrderResponseDTO.setId(id);
        response.setData(storageOrderResponseDTO);
        return response;
    }

    @Operation(summary = "发送出库通知单", description = "创建并发送出库通知单到RFID系统")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "出库通知单创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/send-outboundorder")
    public RfidApiResponseDTO<StorageOrderResponseDTO> sendOutBoundOrder(
            @Parameter(description = "出库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<StorageOrderRequestDTO> requestDTO){
        RfidApiResponseDTO<StorageOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("出库通知单不存在");
            return response;
        }

        StorageOrderRequestDTO storageOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(storageOrderRequestDTO.getOrderNo())) {
            response.setStatus(false);
            response.setMessage("出库通知单号不存在");
            return response;
        }

        Long id = tagStorageOrderService.saveOutboundTagStorageOrder(requestDTO.getTimeStamp(), storageOrderRequestDTO.getOrderNo(), storageOrderRequestDTO.getItems());
        StorageOrderResponseDTO storageOrderResponseDTO = new StorageOrderResponseDTO();
        storageOrderResponseDTO.setOrderNo(storageOrderResponseDTO.getOrderNo());
        storageOrderResponseDTO.setId(id);
        response.setData(storageOrderResponseDTO);
        return response;
    }

    @Operation(summary = "取消出库通知单", description = "取消已创建的出库通知单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "出库通知单取消成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或订单不能被取消"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/cancel-outboundorder")
    public RfidApiResponseDTO<CancelStorageOrderResponseDTO> cancelOutBoundOrder(
            @Parameter(description = "取消出库通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<CancelStorageOrderRequestDTO> requestDTO){
        RfidApiResponseDTO<CancelStorageOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("出库通知单不存在");
            return response;
        }

        CancelStorageOrderRequestDTO cancelStorageOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(cancelStorageOrderRequestDTO.getOrderNo())) {
            response.setStatus(false);
            response.setMessage("出库通知单号不存在");
            return response;
        }

        String orderNo = cancelStorageOrderRequestDTO.getOrderNo();
        boolean canCancel = tagStorageOrderService.checkStorageOrderCancelable(orderNo, PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);
        if (!canCancel) {
            response.setStatus(false);
            response.setMessage("出库通知单不能被取消");
            return response;
        }

        Long id = tagStorageOrderService.cancelTagStorageOrder(requestDTO.getTimeStamp(), orderNo, PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND);

        CancelStorageOrderResponseDTO storageOrderResponseDTO = new CancelStorageOrderResponseDTO();
        storageOrderResponseDTO.setOrderNo(orderNo);
        storageOrderResponseDTO.setId(id);
        response.setData(storageOrderResponseDTO);
        return response;
    }

    @Operation(summary = "发送盘点通知单", description = "创建并发送盘点通知单到RFID系统")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "盘点通知单创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/send-inventoryorder")
    public RfidApiResponseDTO<StorageOrderResponseDTO> sendInventoryOrder(
            @Parameter(description = "盘点通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<StorageOrderRequestDTO> requestDTO){
        RfidApiResponseDTO<StorageOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("盘点通知单不存在");
            return response;
        }

        StorageOrderRequestDTO storageOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(storageOrderRequestDTO.getOrderNo())) {
            response.setStatus(false);
            response.setMessage("盘点通知单号不存在");
            return response;
        }

        Long id = tagStorageOrderService.saveInventoryTagStorageOrder(requestDTO.getTimeStamp(), storageOrderRequestDTO.getOrderNo(), storageOrderRequestDTO.getItems());
        StorageOrderResponseDTO storageOrderResponseDTO = new StorageOrderResponseDTO();
        storageOrderResponseDTO.setOrderNo(storageOrderResponseDTO.getOrderNo());
        storageOrderResponseDTO.setId(id);
        response.setData(storageOrderResponseDTO);
        return response;
    }

    @Operation(summary = "取消盘点通知单", description = "取消已创建的盘点通知单")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "盘点通知单取消成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误或订单不能被取消"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping(value = "/cancel-inventoryorder")
    public RfidApiResponseDTO<CancelStorageOrderResponseDTO> cancelInventoryOrder(
            @Parameter(description = "取消盘点通知单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<CancelStorageOrderRequestDTO> requestDTO){
        RfidApiResponseDTO<CancelStorageOrderResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("盘点通知单不存在");
            return response;
        }

        CancelStorageOrderRequestDTO cancelStorageOrderRequestDTO = requestDTO.getData();
        if (StringUtils.isBlank(cancelStorageOrderRequestDTO.getOrderNo())) {
            response.setStatus(false);
            response.setMessage("盘点通知单号不存在");
            return response;
        }

        String orderNo = cancelStorageOrderRequestDTO.getOrderNo();
        boolean canCancel = tagStorageOrderService.checkStorageOrderCancelable(orderNo, PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);
        if (!canCancel) {
            response.setStatus(false);
            response.setMessage("盘点通知单不能被取消");
            return response;
        }

        Long id = tagStorageOrderService.cancelTagStorageOrder(requestDTO.getTimeStamp(), orderNo, PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND);

        CancelStorageOrderResponseDTO storageOrderResponseDTO = new CancelStorageOrderResponseDTO();
        storageOrderResponseDTO.setOrderNo(orderNo);
        storageOrderResponseDTO.setId(id);
        response.setData(storageOrderResponseDTO);
        return response;
    }
}
