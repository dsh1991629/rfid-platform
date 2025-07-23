package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "入库通知单数据传输对象")
public class InBoundOrderRequestDTO implements Serializable {

    @Schema(description = "WMS系统入库通知单号")
    private String orderNoWMS;

    @Schema(description = "ERP系统入库通知单号")
    private String orderNoERP;

    @Schema(description = "通知单类型 CGRK=采购入库、B2BRK=门店退货入库")
    private String orderType;

    @Schema(description = "收货仓库")
    private String wh;

    @Schema(description = "入库通知单数据详情")
    private List<InBoundOrderItemRequestDTO> items;

}
