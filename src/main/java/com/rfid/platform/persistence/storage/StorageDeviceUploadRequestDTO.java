package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "设备通知单扫描上传数据传输对象")
@Data
public class StorageDeviceUploadRequestDTO implements Serializable {

    @Schema(description = "通知单号")
    private String orderNo;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "箱外码")
    private String boxCode;

    @Schema(description = "扫描EPC数量")
    private Integer rfidCnt;

    @Schema(description = "扫描EPC数组")
    private List<String> epcs;

    @Schema(description = "用于指示通知单操作是否完成，为 true 时，不限定 epcs 的值")
    private Boolean oprtEnd = false;

}
