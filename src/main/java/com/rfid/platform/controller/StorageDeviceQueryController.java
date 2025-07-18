package com.rfid.platform.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rfid.platform.config.PlatformRestProperties;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.LabelInfoRequestDTO;
import com.rfid.platform.persistence.storage.LabelInfoResponseDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryOrderDetailDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryOrderDetailItemDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryRequestDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryResponseDTO;
import com.rfid.platform.service.TagRestService;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderResultService;
import com.rfid.platform.service.TagStorageOrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private TagRestService tagRestService;

    @Autowired
    private PlatformRestProperties platformRestProperties;



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
                detailDTO.setOrderType(e.getOrderType());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNo());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
                    List<StorageCheckQueryOrderDetailItemDTO> items = tagStorageOrderDetailBeans.stream().map(entry -> {
                        String productCode = entry.getProductCode();

                        StorageCheckQueryOrderDetailItemDTO itemDTO = new StorageCheckQueryOrderDetailItemDTO();
                        itemDTO.setProductCode(productCode);
                        itemDTO.setSku(entry.getSku());
                        
                        // 计算总件数
                        int totalCount = entry.getQuantity();
                        itemDTO.setTotalCount(totalCount);
                        
                        // 查询完成数
                        int progress = tagStorageOrderResultService.countCompletedByOrderNoAndProductCode(e.getOrderNo(), productCode);
                        itemDTO.setProgress(progress);

                        itemDTO.setBoxCnt(entry.getBoxCnt());
                        itemDTO.setBoxCodes(StringUtils.isNotBlank(entry.getBoxCodes()) ? Arrays.asList(entry.getBoxCodes().split(",")) : List.of());
                        
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
                detailDTO.setOrderType(e.getOrderType());

                List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(e.getOrderNo());
                // 按照productCode分组，遍历productCode，生成StorageCheckQueryOrderDetailItemDTO集合
                if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {

                    List<StorageCheckQueryOrderDetailItemDTO> items = tagStorageOrderDetailBeans.stream().map(entry -> {
                        String productCode = entry.getProductCode();

                        StorageCheckQueryOrderDetailItemDTO itemDTO = new StorageCheckQueryOrderDetailItemDTO();
                        itemDTO.setProductCode(productCode);
                        itemDTO.setSku(entry.getSku());

                        // 计算总件数
                        int totalCount = entry.getQuantity();
                        itemDTO.setTotalCount(totalCount);

                        // 查询完成数
                        int progress = tagStorageOrderResultService.countCompletedByOrderNoAndProductCode(e.getOrderNo(), productCode);
                        itemDTO.setProgress(progress);

                        itemDTO.setBoxCnt(entry.getBoxCnt());
                        itemDTO.setBoxCodes(StringUtils.isNotBlank(entry.getBoxCodes()) ? Arrays.asList(entry.getBoxCodes().split(",")) : List.of());

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

                    List<StorageCheckQueryOrderDetailItemDTO> items = tagStorageOrderDetailBeans.stream().map(entry -> {
                        String productCode = entry.getProductCode();

                        StorageCheckQueryOrderDetailItemDTO itemDTO = new StorageCheckQueryOrderDetailItemDTO();
                        itemDTO.setProductCode(productCode);
                        itemDTO.setSku(entry.getSku());

                        // 计算总件数
                        int totalCount = entry.getQuantity();
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


    @ApiOperation(value = "查询吊牌信息", notes = "查询吊牌信息")
    @PostMapping(value = "/getlabelinfo")
    public RfidApiResponseDTO<LabelInfoResponseDTO> getLabelInfo (
            @ApiParam(value = "吊牌查询请求", required = true) @RequestBody RfidApiRequestDTO<LabelInfoRequestDTO> requestDTO) {
        RfidApiResponseDTO<LabelInfoResponseDTO> response = RfidApiResponseDTO.success();

        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getData())) {
            response.setStatus(false);
            response.setMessage("吊牌查询对象不存在");
            return response;
        }

        try{
            LabelInfoRequestDTO labelInfoRequestDTO = requestDTO.getData();
            JSONObject reqObject = JSONObject.parseObject(JSON.toJSONString(labelInfoRequestDTO));
            JSONObject jsonObject =
                    tagRestService.executeRestPostOptions(platformRestProperties.getVersion(), platformRestProperties.getSkuUrl(), reqObject);
            if (Objects.nonNull(jsonObject)) {
                RfidApiResponseDTO<LabelInfoResponseDTO> labelInfoResponseDTO =
                        jsonObject.toJavaObject(RfidApiResponseDTO.class);
                response.setData(labelInfoResponseDTO.getData());
            }
        } catch (Exception e) {
            response.setStatus(false);
            response.setMessage("查询出错，" + e);
        }

        return response;
    }

}
