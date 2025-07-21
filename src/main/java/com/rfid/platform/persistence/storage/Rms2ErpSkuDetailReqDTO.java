package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "sku基本信息下载请求数据传输对象")
@Data
public class Rms2ErpSkuDetailReqDTO implements Serializable {

    @Schema(description = "sku")
    private String sku;
}
