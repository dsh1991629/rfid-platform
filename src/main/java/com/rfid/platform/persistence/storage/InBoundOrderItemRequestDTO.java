package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "入库通知单详情数据传输对象")
public class InBoundOrderItemRequestDTO implements Serializable {

    @Schema(description = "PRODUCT_CODE")
    private String productCode;

    @Schema(description = "SKU")
    private String sku;

    @Schema(description = "件数")
    private Integer qty;

}
