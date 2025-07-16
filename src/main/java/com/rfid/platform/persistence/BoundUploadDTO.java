package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通知单上传WMS数据传输对象
 */
@Schema(description = "通知单上传WMS数据传输对象")
@Data
public class BoundUploadDTO {

    @Schema(description = "通知单号")
    private String orderNo;

}