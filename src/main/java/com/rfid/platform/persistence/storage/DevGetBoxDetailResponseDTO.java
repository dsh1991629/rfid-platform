package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "查询箱内明细详情响应数据传输对象")
@Data
public class DevGetBoxDetailResponseDTO implements Serializable {


    @Schema(description = "SKU码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "RFID码")
    private List<String> rfids;
}
