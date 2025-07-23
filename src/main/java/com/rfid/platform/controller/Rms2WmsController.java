package com.rfid.platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.PlatformRestProperties;
import com.rfid.platform.entity.AccountBean;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.persistence.OrderUploadRequestDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.InBoundUploadDetailRequestDTO;
import com.rfid.platform.persistence.storage.InBoundUploadRequestDTO;
import com.rfid.platform.persistence.storage.InBoundUploadResponseDTO;
import com.rfid.platform.persistence.storage.InventoryUploadDetailRequestDTO;
import com.rfid.platform.persistence.storage.InventoryUploadRequestDTO;
import com.rfid.platform.persistence.storage.InventoryUploadResponseDTO;
import com.rfid.platform.persistence.storage.OutBoundUploadDetailItemRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundUploadDetailRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundUploadRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundUploadResponseDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.TagRestService;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderResultService;
import com.rfid.platform.service.TagStorageOrderService;
import com.rfid.platform.util.TimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "RMS到WMS接口", description = "RMS系统与WMS系统之间的数据交互接口")
@RestController
@RequestMapping(value = "/rfid")
public class Rms2WmsController {

    @Autowired
    private TagStorageOrderService tagStorageOrderService;

    @Autowired
    private TagStorageOrderDetailService tagStorageOrderDetailService;

    @Autowired
    private TagStorageOrderResultService tagStorageOrderResultService;

    @Autowired
    private TagRestService tagRestService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PlatformRestProperties platformRestProperties;


    @Operation(summary = "上传入库明细", description = "RMS发送入库明细到WMS系统")
    @PostMapping(value = "/upload-inbounddetails")
    public RfidApiResponseDTO<InBoundUploadResponseDTO> uploadInBoundOrderDetail(@RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO){
        RfidApiResponseDTO<InBoundUploadResponseDTO> responseDTO = RfidApiResponseDTO.success();
        if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS通知单不存在");
            return responseDTO;
        }

        OrderUploadRequestDTO orderUploadRequestDTO = requestDTO.getData();
        String orderNoRms = orderUploadRequestDTO.getOrderNoRMS();
        if (StringUtils.isBlank(orderNoRms)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS通知单号不存在");
            return responseDTO;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNoRms);
        if (Objects.isNull(tagStorageOrderBean)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("通知单数据不存在");
            return responseDTO;
        }

        Integer orderState = tagStorageOrderBean.getState();
        if (!PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED.equals(orderState)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("通知单未完成");
            return responseDTO;
        }

        String url = getUrlString(tagStorageOrderBean);

        if (StringUtils.isBlank(url)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("入库通知单上传WMS地址未配置");
            return responseDTO;
        }

        RfidApiRequestDTO<InBoundUploadRequestDTO> uploadReqDTO = new RfidApiRequestDTO<>();
        uploadReqDTO.setVersion(platformRestProperties.getVersion());
        uploadReqDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        InBoundUploadRequestDTO inBoundUploadRequestDTO = createInBoundUploadDTO(tagStorageOrderBean);
        uploadReqDTO.setData(inBoundUploadRequestDTO);

        responseDTO = tagRestService.executeRestPostOptions(url, uploadReqDTO,
                new TypeReference<RfidApiResponseDTO<InBoundUploadResponseDTO>>() {}
        );

        return responseDTO;
    }

    private InBoundUploadRequestDTO createInBoundUploadDTO(TagStorageOrderBean tagStorageOrderBean) {
        InBoundUploadRequestDTO inBoundUploadRequestDTO = new InBoundUploadRequestDTO();
        String orderNoWms = tagStorageOrderBean.getOrderNoWms();
        inBoundUploadRequestDTO.setOrderNoWMS(orderNoWms);
        inBoundUploadRequestDTO.setWh(tagStorageOrderBean.getWh());

        List<TagStorageOrderResultBean> storageOrderResultBeans =
                tagStorageOrderResultService.listTagStorageOrderResults(tagStorageOrderBean.getOrderNoRms());

        List<InBoundUploadDetailRequestDTO> inBoundUploadDetailRequestDTOS = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(storageOrderResultBeans)) {
            // 按boxCode分组
            Map<String, List<TagStorageOrderResultBean>> groupedByBoxCode = storageOrderResultBeans.stream()
                    .collect(Collectors.groupingBy(TagStorageOrderResultBean::getBoxCode));
            

            // 遍历groupedByBoxCode
            for (Map.Entry<String, List<TagStorageOrderResultBean>> boxEntry : groupedByBoxCode.entrySet()) {
                String boxCode = boxEntry.getKey();
                List<TagStorageOrderResultBean> boxResults = boxEntry.getValue();
                // 设置sku
                if (CollectionUtils.isNotEmpty(boxResults)) {
                    String productCode = boxResults.get(0).getProductCode();

                    InBoundUploadDetailRequestDTO inBoundUploadDetailRequestDTO = new InBoundUploadDetailRequestDTO();
                    inBoundUploadDetailRequestDTO.setBoxCode(boxCode);
                    inBoundUploadDetailRequestDTO.setProductCode(boxResults.get(0).getProductCode());


                    String sku = tagStorageOrderDetailService.getSkuByOrderNoRmsAndProductCode(tagStorageOrderBean.getOrderNoRms(), productCode);
                    if (StringUtils.isNotBlank(sku)) {
                        inBoundUploadDetailRequestDTO.setSku(sku);
                    }

                    // 收集所有rfid
                    List<String> rfids = boxResults.stream()
                            .map(TagStorageOrderResultBean::getEpc)
                            .collect(Collectors.toList());
                    inBoundUploadDetailRequestDTO.setRfids(rfids);

                    inBoundUploadDetailRequestDTOS.add(inBoundUploadDetailRequestDTO);

                }
            }

            inBoundUploadRequestDTO.setItems(inBoundUploadDetailRequestDTOS);
        }

        return inBoundUploadRequestDTO;
    }



    @Operation(summary = "上传出库明细", description = "RMS发送出库明细到WMS系统")
    @PostMapping(value = "/upload-outbounddetails")
    public RfidApiResponseDTO<OutBoundUploadResponseDTO> uploadOutBoundOrderDetail(@RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO){
        RfidApiResponseDTO<OutBoundUploadResponseDTO> responseDTO = RfidApiResponseDTO.success();
        if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS通知单不存在");
            return responseDTO;
        }

        OrderUploadRequestDTO orderUploadRequestDTO = requestDTO.getData();
        String orderNoRms = orderUploadRequestDTO.getOrderNoRMS();
        if (StringUtils.isBlank(orderNoRms)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS通知单号不存在");
            return responseDTO;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNoRms);
        if (Objects.isNull(tagStorageOrderBean)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("通知单数据不存在");
            return responseDTO;
        }

        Integer orderState = tagStorageOrderBean.getState();
        if (!PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED.equals(orderState)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("通知单未完成");
            return responseDTO;
        }

        String url = getUrlString(tagStorageOrderBean);

        if (StringUtils.isBlank(url)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("出库通知单上传WMS地址未配置");
            return responseDTO;
        }

        RfidApiRequestDTO<OutBoundUploadRequestDTO> uploadReqDTO = new RfidApiRequestDTO<>();
        uploadReqDTO.setVersion(platformRestProperties.getVersion());
        uploadReqDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        OutBoundUploadRequestDTO inBoundUploadRequestDTO = createOutBoundUploadDTO(tagStorageOrderBean);
        uploadReqDTO.setData(inBoundUploadRequestDTO);

        responseDTO = tagRestService.executeRestPostOptions(url, uploadReqDTO,
                new TypeReference<RfidApiResponseDTO<OutBoundUploadResponseDTO>>() {}
        );

        return responseDTO;
    }


    private OutBoundUploadRequestDTO createOutBoundUploadDTO(TagStorageOrderBean tagStorageOrderBean) {
        OutBoundUploadRequestDTO outBoundUploadRequestDTO = new OutBoundUploadRequestDTO();
        String orderNoWms = tagStorageOrderBean.getOrderNoWms();
        outBoundUploadRequestDTO.setOrderNoWMS(orderNoWms);
        outBoundUploadRequestDTO.setWh(tagStorageOrderBean.getWh());

        List<TagStorageOrderResultBean> storageOrderResultBeans =
                tagStorageOrderResultService.listTagStorageOrderResults(tagStorageOrderBean.getOrderNoRms());
        if (CollectionUtils.isNotEmpty(storageOrderResultBeans)) {
            // 按boxCode分组
            Map<String, List<TagStorageOrderResultBean>> groupedByBoxCode = storageOrderResultBeans.stream()
                    .collect(Collectors.groupingBy(TagStorageOrderResultBean::getBoxCode));

            List<OutBoundUploadDetailRequestDTO> outBoundUploadDetailRequestDTOS = new ArrayList<>();

            // 遍历groupedByBoxCode
            for (Map.Entry<String, List<TagStorageOrderResultBean>> boxEntry : groupedByBoxCode.entrySet()) {
                OutBoundUploadDetailRequestDTO outBoundUploadDetailRequestDTO = new OutBoundUploadDetailRequestDTO();

                String boxCode = boxEntry.getKey();
                outBoundUploadDetailRequestDTO.setBoxCode(boxCode);

                List<TagStorageOrderResultBean> boxResults = boxEntry.getValue();

                // 根据productCode分组
                Map<String, List<TagStorageOrderResultBean>> groupedByProductCode = boxResults.stream()
                        .collect(Collectors.groupingBy(TagStorageOrderResultBean::getProductCode));

                List<OutBoundUploadDetailItemRequestDTO> outBoundUploadDetailItemRequestDTOS = new ArrayList<>();
                // 遍历productCode分组
                for (Map.Entry<String, List<TagStorageOrderResultBean>> productEntry : groupedByProductCode.entrySet()) {
                    OutBoundUploadDetailItemRequestDTO outBoundUploadDetailItemRequestDTO = new OutBoundUploadDetailItemRequestDTO();

                    String productCode = productEntry.getKey();
                    outBoundUploadDetailItemRequestDTO.setProductCode(productCode);

                    List<TagStorageOrderResultBean> productResults = productEntry.getValue();

                    // 设置sku
                    if (CollectionUtils.isNotEmpty(productResults)) {
                        String sku = tagStorageOrderDetailService.getSkuByOrderNoRmsAndProductCode(tagStorageOrderBean.getOrderNoRms(), productCode);
                        if (StringUtils.isNotBlank(sku)) {
                            outBoundUploadDetailItemRequestDTO.setSku(sku);
                        }
                    }

                    // 收集所有rfid
                    List<String> rfids = productResults.stream()
                            .map(TagStorageOrderResultBean::getEpc)
                            .collect(Collectors.toList());

                    outBoundUploadDetailItemRequestDTO.setRfids(rfids);
                    outBoundUploadDetailItemRequestDTOS.add(outBoundUploadDetailItemRequestDTO);
                }

                outBoundUploadDetailRequestDTO.setBoxItems(outBoundUploadDetailItemRequestDTOS);
                outBoundUploadDetailRequestDTOS.add(outBoundUploadDetailRequestDTO);

            }

            outBoundUploadRequestDTO.setItems(outBoundUploadDetailRequestDTOS);
        }

        return outBoundUploadRequestDTO;
    }



    @Operation(summary = "上传盘点明细", description = "RMS发送盘点明细到WMS系统")
    @PostMapping(value = "/upload-inventorydetails")
    public RfidApiResponseDTO<InventoryUploadResponseDTO> uploadInventoryOrderDetail(@RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO){
        RfidApiResponseDTO<InventoryUploadResponseDTO> responseDTO = RfidApiResponseDTO.success();
        if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS通知单不存在");
            return responseDTO;
        }

        OrderUploadRequestDTO orderUploadRequestDTO = requestDTO.getData();
        String orderNoRms = orderUploadRequestDTO.getOrderNoRMS();
        if (StringUtils.isBlank(orderNoRms)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS通知单号不存在");
            return responseDTO;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNoRms);
        if (Objects.isNull(tagStorageOrderBean)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("通知单数据不存在");
            return responseDTO;
        }

        Integer orderState = tagStorageOrderBean.getState();
        if (!PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED.equals(orderState)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("通知单未完成");
            return responseDTO;
        }

        String url = getUrlString(tagStorageOrderBean);

        if (StringUtils.isBlank(url)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("盘点通知单上传WMS地址未配置");
            return responseDTO;
        }

        RfidApiRequestDTO<InventoryUploadRequestDTO> uploadReqDTO = new RfidApiRequestDTO<>();
        uploadReqDTO.setVersion(platformRestProperties.getVersion());
        uploadReqDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        InventoryUploadRequestDTO inventoryUploadRequestDTO = createInventoryUploadDTO(tagStorageOrderBean);
        uploadReqDTO.setData(inventoryUploadRequestDTO);

        responseDTO = tagRestService.executeRestPostOptions(url, uploadReqDTO,
                new TypeReference<RfidApiResponseDTO<InventoryUploadResponseDTO>>() {}
        );

        return responseDTO;
    }


    private InventoryUploadRequestDTO createInventoryUploadDTO(TagStorageOrderBean tagStorageOrderBean) {
        InventoryUploadRequestDTO inventoryUploadRequestDTO = new InventoryUploadRequestDTO();
        String orderNoWms = tagStorageOrderBean.getOrderNoWms();
        inventoryUploadRequestDTO.setOrderNoWMS(orderNoWms);
        inventoryUploadRequestDTO.setWh(tagStorageOrderBean.getWh());

        AccountBean accountBean = accountService.getAccountByPk(AccountContext.getAccountId());
        if (Objects.nonNull(accountBean)) {
            inventoryUploadRequestDTO.setUserNo(accountBean.getCode());
        }

        List<TagStorageOrderDetailBean> tagStorageOrderDetailBeans = tagStorageOrderDetailService.listTagStorageOrderDetails(tagStorageOrderBean.getOrderNoRms());
        if (CollectionUtils.isNotEmpty(tagStorageOrderDetailBeans)) {
            // 累加所有明细的quantity和boxCnt
            Integer totalQuantity = tagStorageOrderDetailBeans.stream()
                    .mapToInt(detail -> detail.getQuantity() != null ? detail.getQuantity() : 0)
                    .sum();
            Integer totalBoxCnt = tagStorageOrderDetailBeans.stream()
                    .mapToInt(detail -> detail.getBoxCnt() != null ? detail.getBoxCnt() : 0)
                    .sum();
            
            inventoryUploadRequestDTO.setQuantity(totalQuantity);
            inventoryUploadRequestDTO.setBoxCnt(totalBoxCnt);
        }

        List<TagStorageOrderResultBean> storageOrderResultBeans =
                tagStorageOrderResultService.listTagStorageOrderResults(tagStorageOrderBean.getOrderNoRms());

        List<InventoryUploadDetailRequestDTO> inventoryUploadDetailRequestDTOS = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(storageOrderResultBeans)) {
            // 按boxCode分组
            Map<String, List<TagStorageOrderResultBean>> groupedByBoxCode = storageOrderResultBeans.stream()
                    .collect(Collectors.groupingBy(TagStorageOrderResultBean::getBoxCode));


            // 遍历groupedByBoxCode
            for (Map.Entry<String, List<TagStorageOrderResultBean>> boxEntry : groupedByBoxCode.entrySet()) {
                String boxCode = boxEntry.getKey();
                List<TagStorageOrderResultBean> boxResults = boxEntry.getValue();
                // 设置sku
                if (CollectionUtils.isNotEmpty(boxResults)) {
                    String productCode = boxResults.get(0).getProductCode();

                    InventoryUploadDetailRequestDTO inventoryUploadDetailRequestDTO = new InventoryUploadDetailRequestDTO();
                    inventoryUploadDetailRequestDTO.setBoxCode(boxCode);
                    inventoryUploadDetailRequestDTO.setProductCode(boxResults.get(0).getProductCode());


                    String sku = tagStorageOrderDetailService.getSkuByOrderNoRmsAndProductCode(tagStorageOrderBean.getOrderNoRms(), productCode);
                    if (StringUtils.isNotBlank(sku)) {
                        inventoryUploadDetailRequestDTO.setSku(sku);
                    }

                    // 收集所有rfid
                    List<String> rfids = boxResults.stream()
                            .map(TagStorageOrderResultBean::getEpc)
                            .collect(Collectors.toList());
                    inventoryUploadDetailRequestDTO.setRfids(rfids);

                    inventoryUploadDetailRequestDTOS.add(inventoryUploadDetailRequestDTO);

                }
            }

            inventoryUploadRequestDTO.setBoxDetails(inventoryUploadDetailRequestDTOS);
        }

        return inventoryUploadRequestDTO;
    }

    private String getUrlString(TagStorageOrderBean tagStorageOrderBean) {
        String url = "";
        Integer type = tagStorageOrderBean.getType();
        if (PlatformConstant.STORAGE_ORDER_TYPE.IN_BOUND.equals(type)) {
            url = platformRestProperties.getInBoundUploadUrl();
        }
        if (PlatformConstant.STORAGE_ORDER_TYPE.OUT_BOUND.equals(type)) {
            url = platformRestProperties.getOutBoundUploadUrl();
        }
        if (PlatformConstant.STORAGE_ORDER_TYPE.INVENTORY_BOUND.equals(type)) {
            url = platformRestProperties.getInventoryUploadUrl();
        }
        return url;
    }


}
