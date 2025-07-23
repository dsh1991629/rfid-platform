package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "盘点通知单数据传输对象")
public class InventoryOrderRequestDTO implements Serializable {

    @Schema(description = "WMS系统入库通知单号，如果是RMS自建盘点单没有值")
    private String orderNoWMS;

    @Schema(description = "ERP系统入库通知单号，如果是RMS自建盘点单没有值")
    private String orderNoERP;

    @Schema(description = "盘点通知单数据详情")
    private List<InventoryOrderItemRequestDTO> items;

}
