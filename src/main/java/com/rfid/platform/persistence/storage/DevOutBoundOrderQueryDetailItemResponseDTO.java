package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "出库通知单明细响应数据传输对象")
public class DevOutBoundOrderQueryDetailItemResponseDTO implements Serializable {

    @Schema(description = "SKU编码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "总数")
    private Integer qty;

    @Schema(description = "进度")
    private DevOutBoundOrderQueryDetailItemProgressResponseDTO progress;


}
