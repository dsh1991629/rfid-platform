package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "通知单分页查询数据传输对象")
@Data
public class StorageOrderPageQueryDTO implements Serializable {

    @Schema(description = "RMS通知单号")
    private String orderNoRms;

    @Schema(description = "类型 1-入库单 2-出库单 3-盘点单")
    private Integer type;

    @Schema(description = "状态 1-WMS下发 2-设备盘点中 3-设备盘点完成 4-推送WMS完成 5-取消")
    private Integer state;

    @Schema(description = "开始时间, 格式: 2025-08-01")
    private String startDate;

    @Schema(description = "结束时间, 格式: 2025-08-05")
    private String endDate;

}
