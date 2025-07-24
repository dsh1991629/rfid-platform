package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "物流信息请求数据传输对象")
@Data
public class Rms2ErpShippingInfoReqDTO implements Serializable {

    @Schema(description = "通知单号")
    private String orderID_ERP;
    
    @Schema(description = "物流信息")
    private Rms2ErpShippingInfoDeliveryReqDTO shippingInfo;

}