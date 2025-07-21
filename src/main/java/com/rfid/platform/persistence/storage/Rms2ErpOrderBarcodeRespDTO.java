package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "条码信息")
public class Rms2ErpOrderBarcodeRespDTO implements Serializable {

    @Schema(description = "条目内容")
    private String content;

    @Schema(description = "SKU款式码")
    private String skuCode;

    @Schema(description = "数量")
    private Integer quantity;
}
