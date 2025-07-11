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

    @Schema(description = "通知单数据详情")
    private List<StorageOrderItemRequestDTO> items;

}
