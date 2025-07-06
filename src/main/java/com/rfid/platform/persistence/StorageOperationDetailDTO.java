package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
public class StorageOperationDetailDTO implements Serializable {
    /**
     * 数量
     */
    @Schema(description = "出入库数量", example = "100", required = true)
    private Integer noticeQuantity;


    /**
     * SKU编码
     */
    @Schema(description = "SKU编码", required = true)
    private String skuCode;
}
