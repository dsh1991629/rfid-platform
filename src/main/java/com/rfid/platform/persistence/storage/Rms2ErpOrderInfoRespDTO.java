package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "通知单基础信息响应数据传输对象")
@Data
public class Rms2ErpOrderInfoRespDTO implements Serializable {

    @Schema(description = "货主信息")
    private Rms2ErpOrderOwnerInfoRespDTO ownerInfo;
    
    @Schema(description = "收货方信息")
    private Rms2ErpOrderReceiverInfoRespDTO receiverInfo;
    
    @Schema(description = "发货方信息")
    private Rms2ErpOrderSenderInfoRespDTO senderInfo;
    
    @Schema(description = "条码列表")
    private List<Rms2ErpOrderBarcodeRespDTO> barcodeList;
}
