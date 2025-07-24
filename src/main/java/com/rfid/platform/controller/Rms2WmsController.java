package com.rfid.platform.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.PlatformRestProperties;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.entity.TagStorageOrderResultBean;
import com.rfid.platform.persistence.OrderUploadRequestDTO;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.B2CDetailsRequestDTO;
import com.rfid.platform.persistence.storage.B2CDetailsResponseDTO;
import com.rfid.platform.persistence.storage.InBoundUploadDetailRequestDTO;
import com.rfid.platform.persistence.storage.InBoundUploadRequestDTO;
import com.rfid.platform.persistence.storage.InBoundUploadResponseDTO;
import com.rfid.platform.persistence.storage.InventoryUploadDetailRequestDTO;
import com.rfid.platform.persistence.storage.InventoryUploadRequestDTO;
import com.rfid.platform.persistence.storage.InventoryUploadResponseDTO;
import com.rfid.platform.persistence.storage.OutBoundUploadDetailRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundUploadRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundUploadResponseDTO;
import com.rfid.platform.persistence.storage.ShippingRequestDTO;
import com.rfid.platform.persistence.storage.ShippingResponseDTO;
import com.rfid.platform.persistence.storage.WhPackDetailsRequestDTO;
import com.rfid.platform.persistence.storage.WhPackDetailsResponseDTO;
import com.rfid.platform.service.AccountService;
import com.rfid.platform.service.TagRestService;
import com.rfid.platform.service.TagStorageOrderDetailService;
import com.rfid.platform.service.TagStorageOrderResultService;
import com.rfid.platform.service.TagStorageOrderService;
import com.rfid.platform.util.TimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public RfidApiResponseDTO<InBoundUploadResponseDTO> uploadInBoundOrderDetail(
            @Parameter(description = "上传入库明细请求数据", required = true)
            @RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO) {
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
                new TypeReference<RfidApiResponseDTO<InBoundUploadResponseDTO>>() {
                }
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


                    TagStorageOrderDetailBean tagStorageOrderDetailBean = tagStorageOrderDetailService.getSkuByOrderNoRmsAndProductCode(tagStorageOrderBean.getOrderNoRms(), productCode);
                    if (Objects.nonNull(tagStorageOrderDetailBean)) {
                        inBoundUploadDetailRequestDTO.setSku(tagStorageOrderDetailBean.getSku());
                        inBoundUploadDetailRequestDTO.setBinLocation(tagStorageOrderDetailBean.getBinLocation());
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
    public RfidApiResponseDTO<OutBoundUploadResponseDTO> uploadOutBoundOrderDetail(
            @Parameter(description = "上传出库明细请求数据", required = true)
            @RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO) {
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
                new TypeReference<RfidApiResponseDTO<OutBoundUploadResponseDTO>>() {
                }
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
            // 按productCode分组
            Map<String, List<TagStorageOrderResultBean>> groupedByProductCode = storageOrderResultBeans.stream()
                    .collect(Collectors.groupingBy(TagStorageOrderResultBean::getProductCode));

            List<OutBoundUploadDetailRequestDTO> outBoundUploadDetailRequestDTOS = new ArrayList<>();

            // 遍历groupedByBoxCode
            for (Map.Entry<String, List<TagStorageOrderResultBean>> productCodeEntry : groupedByProductCode.entrySet()) {
                OutBoundUploadDetailRequestDTO outBoundUploadDetailRequestDTO = new OutBoundUploadDetailRequestDTO();

                String productCode = productCodeEntry.getKey();
                outBoundUploadDetailRequestDTO.setProductCode(productCode);

                List<TagStorageOrderResultBean> productResults = productCodeEntry.getValue();

                // 设置sku
                if (CollectionUtils.isNotEmpty(productResults)) {
                    TagStorageOrderDetailBean tagStorageOrderDetailBean = tagStorageOrderDetailService.getSkuByOrderNoRmsAndProductCode(tagStorageOrderBean.getOrderNoRms(), productCode);
                    if (Objects.nonNull(tagStorageOrderDetailBean)) {
                        outBoundUploadDetailRequestDTO.setSku(tagStorageOrderDetailBean.getSku());
                    }
                }

                // 收集所有rfid
                List<String> rfids = productResults.stream()
                        .map(TagStorageOrderResultBean::getEpc)
                        .collect(Collectors.toList());

                outBoundUploadDetailRequestDTO.setRfids(rfids);

                outBoundUploadDetailRequestDTOS.add(outBoundUploadDetailRequestDTO);

            }

            outBoundUploadRequestDTO.setItems(outBoundUploadDetailRequestDTOS);
        }

        return outBoundUploadRequestDTO;
    }


    @Operation(summary = "上传盘点明细", description = "RMS发送盘点明细到WMS系统")
    @PostMapping(value = "/upload-inventorydetails")
    public RfidApiResponseDTO<InventoryUploadResponseDTO> uploadInventoryOrderDetail(
            @Parameter(description = "上传盘点明细请求数据", required = true)
            @RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO) {
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
                new TypeReference<RfidApiResponseDTO<InventoryUploadResponseDTO>>() {
                }
        );

        return responseDTO;
    }


    private InventoryUploadRequestDTO createInventoryUploadDTO(TagStorageOrderBean tagStorageOrderBean) {
        InventoryUploadRequestDTO inventoryUploadRequestDTO = new InventoryUploadRequestDTO();
        String orderNoWms = tagStorageOrderBean.getOrderNoWms();
        inventoryUploadRequestDTO.setOrderNoWMS(orderNoWms);
        inventoryUploadRequestDTO.setWh(tagStorageOrderBean.getWh());

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


                    TagStorageOrderDetailBean tagStorageOrderDetailBean = tagStorageOrderDetailService.getSkuByOrderNoRmsAndProductCode(tagStorageOrderBean.getOrderNoRms(), productCode);
                    if (Objects.nonNull(tagStorageOrderDetailBean)) {
                        inventoryUploadDetailRequestDTO.setSku(tagStorageOrderDetailBean.getSku());
                        inventoryUploadDetailRequestDTO.setBinLocation(tagStorageOrderDetailBean.getBinLocation());
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


    @Operation(summary = "上传库内装箱明细", description = "RMS发送库内装箱明细到WMS系统")
    @PostMapping(value = "/upload-whpackdetails")
    public RfidApiResponseDTO<WhPackDetailsResponseDTO> whPackDetails(
            @Parameter(description = "上传库内装箱明细请求数据", required = true)
            @RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO) {

        RfidApiResponseDTO<WhPackDetailsResponseDTO> responseDTO = RfidApiResponseDTO.success();
        if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS库内装箱单不存在");
            return responseDTO;
        }

        OrderUploadRequestDTO orderUploadRequestDTO = requestDTO.getData();
        String orderNoRms = orderUploadRequestDTO.getOrderNoRMS();
        if (StringUtils.isBlank(orderNoRms)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS库内装箱单号不存在");
            return responseDTO;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNoRms);
        if (Objects.isNull(tagStorageOrderBean)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("库内装箱单数据不存在");
            return responseDTO;
        }

        Integer orderState = tagStorageOrderBean.getState();
        if (!PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED.equals(orderState)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("库内装箱单未完成");
            return responseDTO;
        }

        String url = platformRestProperties.getWhPackDetailsUploadUrl();
        if (StringUtils.isBlank(url)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("库内装箱上传WMS地址未配置");
            return responseDTO;
        }

        RfidApiRequestDTO<WhPackDetailsRequestDTO> uploadReqDTO = new RfidApiRequestDTO<>();
        uploadReqDTO.setVersion(platformRestProperties.getVersion());
        uploadReqDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        WhPackDetailsRequestDTO whPackUploadDetailRequestDTO = createWhPackDetailRequestDTO(tagStorageOrderBean);
        uploadReqDTO.setData(whPackUploadDetailRequestDTO);

        responseDTO = tagRestService.executeRestPostOptions(url, uploadReqDTO,
                new TypeReference<RfidApiResponseDTO<WhPackDetailsResponseDTO>>() {
                }
        );

        return responseDTO;
    }

    private WhPackDetailsRequestDTO createWhPackDetailRequestDTO(TagStorageOrderBean tagStorageOrderBean) {
        // todo
        return null;
    }


    @Operation(summary = "上传B2C发货明细", description = "RMS发送B2C发货明细明细到WMS系统")
    @PostMapping(value = "/upload-b2cdetails")
    public RfidApiResponseDTO<B2CDetailsResponseDTO> b2cDetails(
            @Parameter(description = "上传B2C发货明细请求数据", required = true)
            @RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO) {

        RfidApiResponseDTO<B2CDetailsResponseDTO> responseDTO = RfidApiResponseDTO.success();
        if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS库内装箱单不存在");
            return responseDTO;
        }

        OrderUploadRequestDTO orderUploadRequestDTO = requestDTO.getData();
        String orderNoRms = orderUploadRequestDTO.getOrderNoRMS();
        if (StringUtils.isBlank(orderNoRms)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS库内装箱单号不存在");
            return responseDTO;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNoRms);
        if (Objects.isNull(tagStorageOrderBean)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("库内装箱单数据不存在");
            return responseDTO;
        }

        Integer orderState = tagStorageOrderBean.getState();
        if (!PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED.equals(orderState)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("库内装箱单未完成");
            return responseDTO;
        }

        String url = platformRestProperties.getB2cDetailsUploadUrl();
        if (StringUtils.isBlank(url)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("B2C发货明细上传WMS地址未配置");
            return responseDTO;
        }

        RfidApiRequestDTO<B2CDetailsRequestDTO> uploadReqDTO = new RfidApiRequestDTO<>();
        uploadReqDTO.setVersion(platformRestProperties.getVersion());
        uploadReqDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        B2CDetailsRequestDTO b2CDetailsRequestDTO = createB2CDetailRequestDTO(tagStorageOrderBean);
        uploadReqDTO.setData(b2CDetailsRequestDTO);

        responseDTO = tagRestService.executeRestPostOptions(url, uploadReqDTO,
                new TypeReference<RfidApiResponseDTO<B2CDetailsResponseDTO>>() {
                }
        );

        return responseDTO;
    }

    private B2CDetailsRequestDTO createB2CDetailRequestDTO(TagStorageOrderBean tagStorageOrderBean) {
        // todo
        return null;
    }


    @Operation(summary = "物流下单", description = "RMS发送物流下单明细到WMS系统")
    @PostMapping(value = "/send-shippingrequest")
    public RfidApiResponseDTO<ShippingResponseDTO> shippingRequest(
            @Parameter(description = "物流下单请求数据", required = true)
            @RequestBody RfidApiRequestDTO<OrderUploadRequestDTO> requestDTO) {

        RfidApiResponseDTO<ShippingResponseDTO> responseDTO = RfidApiResponseDTO.success();
        if (Objects.isNull(responseDTO) || Objects.isNull(responseDTO.getData())) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS库内装箱单不存在");
            return responseDTO;
        }

        OrderUploadRequestDTO orderUploadRequestDTO = requestDTO.getData();
        String orderNoRms = orderUploadRequestDTO.getOrderNoRMS();
        if (StringUtils.isBlank(orderNoRms)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("RMS库内装箱单号不存在");
            return responseDTO;
        }

        TagStorageOrderBean tagStorageOrderBean = tagStorageOrderService.queryTagStorageOrderByNo(orderNoRms);
        if (Objects.isNull(tagStorageOrderBean)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("库内装箱单数据不存在");
            return responseDTO;
        }

        Integer orderState = tagStorageOrderBean.getState();
        if (!PlatformConstant.STORAGE_ORDER_STATUS.COMPLETED.equals(orderState)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("库内装箱单未完成");
            return responseDTO;
        }

        String url = platformRestProperties.getB2cDetailsUploadUrl();
        if (StringUtils.isBlank(url)) {
            responseDTO.setStatus(false);
            responseDTO.setMessage("B2C发货明细上传WMS地址未配置");
            return responseDTO;
        }

        RfidApiRequestDTO<ShippingRequestDTO> uploadReqDTO = new RfidApiRequestDTO<>();
        uploadReqDTO.setVersion(platformRestProperties.getVersion());
        uploadReqDTO.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        ShippingRequestDTO shippingRequestDTO = createShippingRequestDTO(tagStorageOrderBean);
        uploadReqDTO.setData(shippingRequestDTO);

        responseDTO = tagRestService.executeRestPostOptions(url, uploadReqDTO,
                new TypeReference<RfidApiResponseDTO<ShippingResponseDTO>>() {
                }
        );

        return responseDTO;
    }

    private ShippingRequestDTO createShippingRequestDTO(TagStorageOrderBean tagStorageOrderBean) {
        // todo
        return null;
    }


}
