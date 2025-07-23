package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "盘点通知单明细响应数据传输对象")
public class DevInventoryOrderQueryDetailItemResponseDTO implements Serializable {

    @Schema(description = "SKU编码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "总数")
    private Integer qty;

    @Schema(description = "箱数量")
    private Integer boxCnt;

    @Schema(description = "进度")
    private DevInventoryOrderQueryDetailItemProgressResponseDTO progress;


}
