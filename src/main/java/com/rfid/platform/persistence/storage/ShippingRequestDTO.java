package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "物流下单请求数据传输对象")
public class ShippingRequestDTO implements Serializable {

    @Schema(description = "WMS系统入库通知单号")
    private String orderNoWMS;

    @Schema(description = "仓库编号")
    private String wh;

}
