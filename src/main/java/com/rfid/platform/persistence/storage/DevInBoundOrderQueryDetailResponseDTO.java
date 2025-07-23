package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "入库通知单结果详情响应数据传输对象")
public class DevInBoundOrderQueryDetailResponseDTO implements Serializable {

    @Schema(description = "WMS入库通知单号")
    private String orderID_WMS;

    @Schema(description = "ERP入库通知单号")
    private String orderID_ERP;

    @Schema(description = "RMS入库通知单号")
    private String orderID_RMS;

    @Schema(description = "入库通知单类型")
    private String orderType;

    @Schema(description = "入库通知单明细")
    private List<DevInBoundOrderQueryDetailItemResponseDTO> details;


}
