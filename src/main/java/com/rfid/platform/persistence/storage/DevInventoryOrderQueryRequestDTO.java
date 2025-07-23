package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "设备查询盘点通知单数据传输对象")
public class DevInventoryOrderQueryRequestDTO implements Serializable {

    @Schema(description = "RMS盘点通知单号码")
    private String orderID_RMS;

    @Schema(description = "开始时间, yyyyMMdd")
    private String timeBegin;

    @Schema(description = "结束时间, yyyyMMdd")
    private String timeEnd;
}
