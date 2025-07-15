package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Schema(description = "设备通知单扫描结果校验请求数据传输对象")
@Data
public class StorageDeviceValidateRequestDTO implements Serializable {

    @Schema(description = "请求单号")
    private String orderNo;

    @Schema(description = "箱外码")
    private String boxCode;

    @Schema(description = "校验数据")
    private List<StorageDeviceValidateDetailRequestDTO> details = new ArrayList<>();

}
