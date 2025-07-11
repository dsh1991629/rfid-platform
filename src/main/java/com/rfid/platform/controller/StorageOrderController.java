package com.rfid.platform.controller;

import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.CancelStorageOrderResponseDTO;
import com.rfid.platform.persistence.storage.CancelStorageOrderRequestDTO;
import com.rfid.platform.persistence.storage.StorageOrderRequestDTO;
import com.rfid.platform.persistence.storage.StorageOrderResponseDTO;
import com.rfid.platform.service.TagStorageOrderService;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rfid")
public class StorageOrderController {

    @Autowired
    private TagStorageOrderService tagStorageOrderService;


    @PostMapping(value = "/send-inboundorder")
    public RfidApiResponseDTO<StorageOrderResponseDTO> sendInBoundOrder(@RequestBody RfidApiRequestDTO<StorageOrderRequestDTO> requestDTO){
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


    @PostMapping(value = "/cancel-inboundorder")
    public RfidApiResponseDTO<CancelStorageOrderResponseDTO> cancelInBoundOrder(@RequestBody RfidApiRequestDTO<CancelStorageOrderRequestDTO> requestDTO){
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


    @PostMapping(value = "/send-outboundorder")
    public RfidApiResponseDTO<StorageOrderResponseDTO> sendOutBoundOrder(@RequestBody RfidApiRequestDTO<StorageOrderRequestDTO> requestDTO){
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


    @PostMapping(value = "/cancel-outboundorder")
    public RfidApiResponseDTO<CancelStorageOrderResponseDTO> cancelOutBoundOrder(@RequestBody RfidApiRequestDTO<CancelStorageOrderRequestDTO> requestDTO){
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


    @PostMapping(value = "/send-inventoryorder")
    public RfidApiResponseDTO<StorageOrderResponseDTO> sendInventoryOrder(@RequestBody RfidApiRequestDTO<StorageOrderRequestDTO> requestDTO){
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


    @PostMapping(value = "/cancel-inventoryorder")
    public RfidApiResponseDTO<CancelStorageOrderResponseDTO> cancelInventoryOrder(@RequestBody RfidApiRequestDTO<CancelStorageOrderRequestDTO> requestDTO){
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
