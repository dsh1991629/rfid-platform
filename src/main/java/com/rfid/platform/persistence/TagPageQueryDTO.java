package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "标签分页查询数据传输对象")
@Data
public class TagPageQueryDTO implements Serializable {

    @Schema(description = "SKU编码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

}
