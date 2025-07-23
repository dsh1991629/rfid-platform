package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "取消出库通知单数据传输对象")
public class CancelInventoryOrderRequestDTO implements Serializable {

    @Schema(description = "WMS出库通知单号, WMS下发的盘点单")
    private String orderNoWMS;

    @Schema(description = "RMS出库通知单号, RMS自建的盘点单")
    private String orderNoRMS;

}
