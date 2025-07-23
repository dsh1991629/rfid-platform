package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "上传盘点明细响应数据传输对象")
@Data
public class InventoryUploadResponseDTO implements Serializable {

    @Schema(description = "WMS盘点单号")
    private String orderNoWMS;

}
