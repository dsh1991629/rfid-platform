package com.rfid.platform.controller;

import com.rfid.platform.config.PlatformRestProperties;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import com.rfid.platform.persistence.storage.Rms2ErpSkuDetailReqDTO;
import com.rfid.platform.persistence.storage.Rms2ErpSkuDetailRespDTO;
import com.rfid.platform.persistence.storage.Rms2ErpOrderInfoReqDTO;
import com.rfid.platform.persistence.storage.Rms2ErpOrderInfoRespDTO;
import com.rfid.platform.persistence.storage.Rms2ErpShippingInfoReqDTO;
import com.rfid.platform.persistence.storage.Rms2ErpShippingInfoRespDTO;
import com.rfid.platform.service.TagRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping(value = "/rfid")
@Tag(name = "RMS到ERP接口", description = "RMS系统与ERP系统之间的数据交互接口")
public class Rms2ErpController {

    @Autowired
    private TagRestService tagRestService;

    @Autowired
    private PlatformRestProperties platformRestProperties;

    @Operation(summary = "获取SKU详情", description = "根据SKU编码获取商品详细信息")
    @PostMapping(value = "/getskudetails")
    public RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO> getSkuDetails(
            @Parameter(description = "SKU详情请求参数", required = true)
            @RequestBody RfidApiRequestDTO<Rms2ErpSkuDetailReqDTO> requestDTO) {
        RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO> responseDTO = tagRestService.executeRestPostOptions(
            platformRestProperties.getGetSkuDetailUrl(), 
            requestDTO, 
            new TypeReference<RfidApiResponseDTO<Rms2ErpSkuDetailRespDTO>>() {}
        );
        
        if (responseDTO == null) {
            responseDTO = new RfidApiResponseDTO<>();
        }
        
        return responseDTO;
    }

    @Operation(summary = "获取订单信息", description = "根据订单号获取订单基础信息")
    @PostMapping(value = "/getorderinfo")
    public RfidApiResponseDTO<Rms2ErpOrderInfoRespDTO> getOrderInfo(
            @Parameter(description = "订单信息请求参数", required = true)
            @RequestBody RfidApiRequestDTO<Rms2ErpOrderInfoReqDTO> requestDTO) {
        RfidApiResponseDTO<Rms2ErpOrderInfoRespDTO> responseDTO = tagRestService.executeRestPostOptions(
            platformRestProperties.getGetGetOrderInfoUrl(), 
            requestDTO, 
            new TypeReference<RfidApiResponseDTO<Rms2ErpOrderInfoRespDTO>>() {}
        );
        
        if (responseDTO == null) {
            responseDTO = new RfidApiResponseDTO<>();
        }
        
        return responseDTO;
    }

    @Operation(summary = "获取物流信息", description = "根据订单号获取物流配送信息")
    @PostMapping(value = "/getshippinginfo")
    public RfidApiResponseDTO<Rms2ErpShippingInfoRespDTO> getShippingInfo(
            @Parameter(description = "物流信息请求参数", required = true)
            @RequestBody RfidApiRequestDTO<Rms2ErpShippingInfoReqDTO> requestDTO) {
        RfidApiResponseDTO<Rms2ErpShippingInfoRespDTO> responseDTO = tagRestService.executeRestPostOptions(
            platformRestProperties.getGetShippingInfoUrl(), 
            requestDTO, 
            new TypeReference<RfidApiResponseDTO<Rms2ErpShippingInfoRespDTO>>() {}
        );
        
        if (responseDTO == null) {
            responseDTO = new RfidApiResponseDTO<>();
        }
        
        return responseDTO;
    }
}
