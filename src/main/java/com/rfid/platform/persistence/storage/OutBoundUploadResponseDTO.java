package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "上传出库明细响应数据传输对象")
@Data
public class OutBoundUploadResponseDTO implements Serializable {

    @Schema(description = "WMS出库单号")
    private String orderNoWMS;
}
