package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "出库通知单结果详情响应数据传输对象")
public class DevOutBoundOrderQueryDetailResponseDTO implements Serializable {

    @Schema(description = "WMS出库通知单号")
    private String orderID_WMS;

    @Schema(description = "ERP出库通知单号")
    private String orderID_ERP;

    @Schema(description = "RMS出库通知单号")
    private String orderID_RMS;

    @Schema(description = "出库通知单类型")
    private String orderType;

    @Schema(description = "出库通知单明细")
    private List<DevOutBoundOrderQueryDetailItemResponseDTO> details;


}
