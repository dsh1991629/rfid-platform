package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "盘点通知单详情数据传输对象")
public class InventoryOrderItemRequestDTO implements Serializable {

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "SKU")
    private String sku;

    @Schema(description = "件数")
    private Integer qty;

    @Schema(description = "箱数量")
    private Integer boxCnt;

    @Schema(description = "货物当前库位")
    private String binLocation;
}
