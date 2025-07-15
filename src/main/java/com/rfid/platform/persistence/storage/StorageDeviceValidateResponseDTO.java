package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "设备通知单扫描结果校验数据传输对象")
@Data
public class StorageDeviceValidateResponseDTO implements Serializable {

    @Schema(description = "校验结果")
    private StorageDeviceValidateDetailResponseDTO data;

}
