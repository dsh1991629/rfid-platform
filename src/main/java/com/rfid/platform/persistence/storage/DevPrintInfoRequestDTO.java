package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "打印信息请求数据传输对象")
@Data
public class DevPrintInfoRequestDTO implements Serializable {

    @Schema(description = "SKU码")
    private String sku;

}
