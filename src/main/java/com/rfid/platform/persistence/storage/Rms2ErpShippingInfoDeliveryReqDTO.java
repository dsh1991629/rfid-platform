package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "物流请求数据传输对象")
@Data
public class Rms2ErpShippingInfoDeliveryReqDTO implements Serializable {

    @Schema(description = "物流公司名")
    private String shippingCompany;

    @Schema(description = "物流单号")
    private String trackingNumber;

    @Schema(description = "发货公司名")
    private String fromCompany;

    @Schema(description = "发货人")
    private String shipper;

    @Schema(description = "发货地址")
    private String shippingAddress;

    @Schema(description = "收货公司名")
    private String toCompany;

    @Schema(description = "收货人")
    private String consignee;

    @Schema(description = "收货地址")
    private String deliveryAddress;

    @Schema(description = "发货时间")
    private String shippingDate;

    @Schema(description = "运输方式")
    private String shippingMethod;
}
