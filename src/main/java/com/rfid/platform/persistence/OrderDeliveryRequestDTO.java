package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "物流上传WMS请求数据传输对象")
@Data
public class OrderDeliveryRequestDTO implements Serializable {

    @Schema(description = "RMS通知单号")
    private String orderNoRMS;

    @Schema(description = "物流单号")
    private String trackingNo;
}
