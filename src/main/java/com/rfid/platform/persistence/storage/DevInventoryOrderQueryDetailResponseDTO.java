package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "盘点通知单结果详情响应数据传输对象")
public class DevInventoryOrderQueryDetailResponseDTO implements Serializable {

    @Schema(description = "WMS盘点通知单号")
    private String orderID_WMS;

    @Schema(description = "ERP盘点通知单号")
    private String orderID_ERP;

    @Schema(description = "RMS盘点通知单号")
    private String orderID_RMS;

    @Schema(description = "盘点通知单明细")
    private List<DevInventoryOrderQueryDetailItemResponseDTO> details;


}
