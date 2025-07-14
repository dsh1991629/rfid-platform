package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通知单数据传输对象")
public class StorageOrderRequestDTO implements Serializable {

    @Schema(description = "通知单号码")
    private String orderNo;

    @Schema(description = "通知单类型 GHSRK=供货商入库、MDTHRK=门店退货入库、WDTHRK=网店退货入库 THGGYSCK=退货给供应商出库，MDPHCK=门店配货出库，WDPHCK=网店配货出库")
    private String orderType;

    @Schema(description = "通知单数据详情")
    private List<StorageOrderItemRequestDTO> items;

}
