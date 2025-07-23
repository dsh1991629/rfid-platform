package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "取消盘点通知单结果数据传输对象")
public class CancelInventoryOrderResponseDTO implements Serializable {

    @Schema(description = "RMS盘点通知单号")
    private String orderNoRms;

}
