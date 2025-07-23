package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "盘点通知单结果数据传输对象")
public class InventoryOrderResponseDTO implements Serializable {

    @Schema(description = "RMS系统盘点通知单号")
    private String orderNoRMS;
}
