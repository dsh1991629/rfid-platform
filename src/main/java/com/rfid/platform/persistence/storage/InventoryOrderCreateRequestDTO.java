package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "RMS盘点通知单创建数据传输对象")
public class InventoryOrderCreateRequestDTO implements Serializable {

    @Schema(description = "仓库编码")
    private String wh;

    @Schema(description = "盘点通知单数据详情")
    private List<InventoryOrderItemRequestDTO> items;

}
