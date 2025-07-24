package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "上传库内盘点数据明细请求数据传输对象")
@Data
public class WhPackUploadDetailRequestDTO implements Serializable {

    @Schema(description = "SKU码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "RFID码")
    private List<String> rfids;

}
