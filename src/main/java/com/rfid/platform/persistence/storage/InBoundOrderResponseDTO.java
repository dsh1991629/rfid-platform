package com.rfid.platform.persistence.storage;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "入库通知单结果数据传输对象")
public class InBoundOrderResponseDTO implements Serializable {

    @Schema(description = "RMS系统入库通知单号")
    private String orderNoRMS;
}
