package com.rfid.platform.controller;

import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryOrderDetailDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryOrderDetailItemDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryRequestDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryResponseDTO;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderResultService;
import com.rfid.platform.service.TagStorageOrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Tag(name = "设备通知单查询", description = "提供给设备入库、出库、盘点通知单查询功能")
@RestController
@RequestMapping(value = "/rfid/dev")
public class StorageDeviceQueryController {

    @Autowired
    private TagStorageOrderService tagStorageOrderService;

    @Autowired
    private TagStorageOrderDetailService tagStorageOrderDetailService;

    @Autowired
    private TagStorageOrderResultService tagStorageOrderResultService;


    @ApiOperation(value = "获取入库通知单", notes = "查询活跃的入库通知单及其详情")
    @PostMapping(value = "/getinboundorder")
    public RfidApiResponseDTO<StorageCheckQueryResponseDTO> getInBoundOrder (
            @ApiParam(value = "入库通知单查询请求", required = true) @RequestBody RfidApiRequestDTO<StorageCheckQueryRequestDTO> requestDTO) {
        RfidApiResponseDTO<StorageCheckQueryResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO)) {
            response.setStatus(false);
            response.setMessage("入库通知单查询对象不存在");
            return response;
        }
        StorageCheckQueryResponseDTO storageCheckQueryResponseDTO = new StorageCheckQueryResponseDTO();

        List<TagStorageOrderBean> tagStorageOrderBeans = tagStorageOrderService.queryActiveInBoundOrders(requestDTO.getData());
        if (CollectionUtils.isNotEmpty(tagStorageOrderBeans)) {
            List<StorageCheckQueryOrderDetailDTO> details = tagStorageOrderBeans.stream().map(e -> {
                StorageCheckQueryOrderDetailDTO detailDTO = new StorageCheckQueryOrderDetailDTO();
                detailDTO.setOrderNo(e.getOrderNo());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNo());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                    Map<String, List<TagStorageOrderDetailBean>> productCodeGroupMap = tagStorageOrderDetailBeans.stream()
                            .collect(Collectors.groupingBy(TagStorageOrderDetailBean::getProductCode));
                    
                    List<StorageCheckQueryOrderDetailItemDTO> items = productCodeGroupMap.entrySet().stream().map(entry -> {
                        String productCode = entry.getKey();
                        List<TagStorageOrderDetailBean> detailBeans = entry.getValue();
                        
                        StorageCheckQueryOrderDetailItemDTO itemDTO = new StorageCheckQueryOrderDetailItemDTO();
                        itemDTO.setProductCode(productCode);
                        
                        // 计算总件数
                        int totalCount = detailBeans.get(0).getQuantity();
                        itemDTO.setTotalCount(totalCount);
                        
                        // 查询完成数
                        int progress = tagStorageOrderResultService.countCompletedByOrderNoAndProductCode(e.getOrderNo(), productCode);
                        itemDTO.setProgress(progress);
                        
                        return itemDTO;
                    }).collect(Collectors.toList());
                    
                    detailDTO.setItems(items);
                }

                return detailDTO;
            }).collect(Collectors.toUnmodifiableList());
            storageCheckQueryResponseDTO.setOrders(details);
        }
        response.setData(storageCheckQueryResponseDTO);
        return response;
    }


    @ApiOperation(value = "获取出库通知单", notes = "查询活跃的出库通知单及其详情")
    @PostMapping(value = "/getoutboundorder")
    public RfidApiResponseDTO<StorageCheckQueryResponseDTO> getOutBoundOrder (
            @ApiParam(value = "出库通知单查询请求", required = true) @RequestBody RfidApiRequestDTO<StorageCheckQueryRequestDTO> requestDTO) {
        RfidApiResponseDTO<StorageCheckQueryResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO)) {
            response.setStatus(false);
            response.setMessage("出库通知单查询对象不存在");
            return response;
        }
        StorageCheckQueryResponseDTO storageCheckQueryResponseDTO = new StorageCheckQueryResponseDTO();

        List<TagStorageOrderBean> tagStorageOrderBeans = tagStorageOrderService.queryActiveOutBoundOrders(requestDTO.getData());
        if (CollectionUtils.isNotEmpty(tagStorageOrderBeans)) {
            List<StorageCheckQueryOrderDetailDTO> details = tagStorageOrderBeans.stream().map(e -> {
                StorageCheckQueryOrderDetailDTO detailDTO = new StorageCheckQueryOrderDetailDTO();
                detailDTO.setOrderNo(e.getOrderNo());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNo());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                    Map<String, List<TagStorageOrderDetailBean>> productCodeGroupMap = tagStorageOrderDetailBeans.stream()
                            .collect(Collectors.groupingBy(TagStorageOrderDetailBean::getProductCode));

                    List<StorageCheckQueryOrderDetailItemDTO> items = productCodeGroupMap.entrySet().stream().map(entry -> {
                        String productCode = entry.getKey();
                        List<TagStorageOrderDetailBean> detailBeans = entry.getValue();

                        StorageCheckQueryOrderDetailItemDTO itemDTO = new StorageCheckQueryOrderDetailItemDTO();
                        itemDTO.setProductCode(productCode);

                        // 计算总件数
                        int totalCount = detailBeans.get(0).getQuantity();
                        itemDTO.setTotalCount(totalCount);

                        // 查询完成数
                        int progress = tagStorageOrderResultService.countCompletedByOrderNoAndProductCode(e.getOrderNo(), productCode);
                        itemDTO.setProgress(progress);

                        return itemDTO;
                    }).collect(Collectors.toList());

                    detailDTO.setItems(items);
                }

                return detailDTO;
            }).collect(Collectors.toUnmodifiableList());
            storageCheckQueryResponseDTO.setOrders(details);
        }
        response.setData(storageCheckQueryResponseDTO);
        return response;
    }


    @ApiOperation(value = "获取盘点通知单", notes = "查询活跃的盘点通知单及其详情")
    @PostMapping(value = "/getinventoryorder")
    public RfidApiResponseDTO<StorageCheckQueryResponseDTO> getInventoryOrder (
            @ApiParam(value = "盘点通知单查询请求", required = true) @RequestBody RfidApiRequestDTO<StorageCheckQueryRequestDTO> requestDTO) {
        RfidApiResponseDTO<StorageCheckQueryResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO)) {
            response.setStatus(false);
            response.setMessage("出库通知单查询对象不存在");
            return response;
        }
        StorageCheckQueryResponseDTO storageCheckQueryResponseDTO = new StorageCheckQueryResponseDTO();

        List<TagStorageOrderBean> tagStorageOrderBeans = tagStorageOrderService.queryActiveInventoryOrders(requestDTO.getData());
        if (CollectionUtils.isNotEmpty(tagStorageOrderBeans)) {
            List<StorageCheckQueryOrderDetailDTO> details = tagStorageOrderBeans.stream().map(e -> {
                StorageCheckQueryOrderDetailDTO detailDTO = new StorageCheckQueryOrderDetailDTO();
                detailDTO.setOrderNo(e.getOrderNo());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNo());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                    Map<String, List<TagStorageOrderDetailBean>> productCodeGroupMap = tagStorageOrderDetailBeans.stream()
                            .collect(Collectors.groupingBy(TagStorageOrderDetailBean::getProductCode));

                    List<StorageCheckQueryOrderDetailItemDTO> items = productCodeGroupMap.entrySet().stream().map(entry -> {
                        String productCode = entry.getKey();
                        List<TagStorageOrderDetailBean> detailBeans = entry.getValue();

                        StorageCheckQueryOrderDetailItemDTO itemDTO = new StorageCheckQueryOrderDetailItemDTO();
                        itemDTO.setProductCode(productCode);

                        // 计算总件数
                        int totalCount = detailBeans.get(0).getQuantity();
                        itemDTO.setTotalCount(totalCount);

                        // 查询完成数
                        int progress = tagStorageOrderResultService.countCompletedByOrderNoAndProductCode(e.getOrderNo(), productCode);
                        itemDTO.setProgress(progress);

                        return itemDTO;
                    }).collect(Collectors.toList());

                    detailDTO.setItems(items);
                }

                return detailDTO;
            }).collect(Collectors.toUnmodifiableList());
            storageCheckQueryResponseDTO.setOrders(details);
        }
        response.setData(storageCheckQueryResponseDTO);
        return response;
    }

}
