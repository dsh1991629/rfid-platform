package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "通知单结果详情数据传输对象")
public class StorageCheckQueryOrderDetailDTO implements Serializable {

    @Schema(description = "通知单号")
    private String orderNo;

    @Schema(description = "通知单详情")
    private List<StorageCheckQueryOrderDetailItemDTO> items;


}
