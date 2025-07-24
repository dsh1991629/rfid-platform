package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "入库数据明细请求数据传输对象")
@Data
public class InBoundUploadDetailRequestDTO implements Serializable {

    @Schema(description = "箱外码")
    private String boxCode;

    @Schema(description = "SKU码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "RFID码")
    private List<String> rfids;

    @Schema(description = "库位")
    private String binLocation;

}
