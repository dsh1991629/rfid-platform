package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "库内装箱数据明细详情数据传输对象")
@Data
public class WhPackDetailsItemRequestDTO implements Serializable {

    @Schema(description = "SKU码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "RFID码")
    private List<String> rfids;


}
