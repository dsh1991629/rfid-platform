package com.rfid.platform.persistence.storage;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "取消入库通知单数据传输对象")
public class CancelInBoundOrderRequestDTO implements Serializable {

    @Schema(description = "WMS入库通知单号")
    private String orderNoWMS;

    @Schema(description = "仓库编码")
    private String wh;

}
