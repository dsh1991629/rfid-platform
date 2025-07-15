package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "设备通知单扫描结果校验详情数据传输对象")
@Data
public class StorageDeviceValidateDetailResponseDTO implements Serializable {

    @Schema(description = "多的部分")
    private List<String> newRfids;

    @Schema(description = "少的部分")
    private List<String> lostRfids;
    
}
