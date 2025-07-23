package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "出库通知单数据传输对象")
public class OutBoundOrderRequestDTO implements Serializable {

    @Schema(description = "WMS系统出库通知单号")
    private String orderNoWMS;

    @Schema(description = "ERP系统出库通知单号")
    private String orderNoERP;

    @Schema(description = "通知单类型 CGTHCK=采购退货出库，B2BCK=B2B出库")
    private String orderType;

    @Schema(description = "收货仓库")
    private String wh;

    @Schema(description = "出库通知单数据详情")
    private List<OutBoundOrderItemRequestDTO> items;

}
