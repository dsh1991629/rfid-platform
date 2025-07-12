package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "设备通知单扫描上传结果数据传输对象")
@Data
public class StorageDeviceUploadResponseDTO implements Serializable {

    @Schema(description = "通知单号")
    private String orderNo;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "本通知单已经扫描的的箱数量")
    private Integer oprtBoxCnt;

    @Schema(description = "本通知单已经扫描的EPC数量")
    private Integer oprtCnt;

}
