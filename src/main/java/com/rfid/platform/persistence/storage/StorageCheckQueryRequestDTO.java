package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "设备查询通知单数据传输对象")
public class StorageCheckQueryRequestDTO implements Serializable {

    @Schema(description = "通知单号码")
    private String orderNo;

    @Schema(description = "开始时间")
    private String timeBegin;

    @Schema(description = "结束时间")
    private String timeEnd;
}
