package com.rfid.platform.persistence.storage;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "取消通知单结果数据传输对象")
public class CancelStorageOrderResponseDTO implements Serializable {

    @Schema(description = "通知单号")
    private String orderNo;

    @Schema(description = "通知单ID")
    private Long id;

}
