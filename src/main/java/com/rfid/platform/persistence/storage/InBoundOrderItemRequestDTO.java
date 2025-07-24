package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "入库通知单详情数据传输对象")
public class InBoundOrderItemRequestDTO implements Serializable {

    @Schema(description = "入库后放置的库位")
    private String binLocation;

    @Schema(description = "SKU码")
    private String sku;

    @Schema(description = "件数")
    private Integer qty;

}
