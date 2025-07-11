package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通知单详情数据传输对象")
public class StorageOrderItemRequestDTO implements Serializable {

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "产品名")
    private String productName;

    @Schema(description = "尺寸")
    private String productSize;

    @Schema(description = "颜色")
    private String productColor;

    @Schema(description = "SKU")
    private String sku;

    @Schema(description = "件数")
    private Integer quantity;

    @Schema(description = "箱数量")
    private Integer boxCnt;

    @Schema(description = "箱外码")
    private List<String> boxCodes;


}
