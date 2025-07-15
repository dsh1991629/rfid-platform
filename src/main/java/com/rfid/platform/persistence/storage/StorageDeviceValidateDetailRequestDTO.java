package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Schema(description = "设备通知单扫描结果校验详情数据传输对象")
@Data
public class StorageDeviceValidateDetailRequestDTO implements Serializable {

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "总数")
    private Integer rfidCnt;

    @Schema(description = "rfid")
    private List<String> rfids = new ArrayList<>();

}
