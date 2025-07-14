package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "设备通知单扫描上传详情数据传输对象")
@Data
public class StorageDeviceUploadDetailRequestDTO implements Serializable {

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "箱外码")
    private String boxCode;

    @Schema(description = "扫描EPC数量")
    private Integer rfidCnt;

    @Schema(description = "扫描EPC数组")
    private List<String> rfids;

}
