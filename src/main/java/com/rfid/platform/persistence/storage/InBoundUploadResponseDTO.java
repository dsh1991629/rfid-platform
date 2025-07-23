package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "上传入库明细响应数据传输对象")
@Data
public class InBoundUploadResponseDTO implements Serializable {

    @Schema(description = "WMS入库单号")
    private String orderNoWMS;

}
