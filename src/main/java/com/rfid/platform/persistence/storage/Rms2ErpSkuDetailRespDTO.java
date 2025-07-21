package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "sku基本信息下载响应数据传输对象")
@Data
public class Rms2ErpSkuDetailRespDTO implements Serializable {

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "图片url")
    private String pic;

}
