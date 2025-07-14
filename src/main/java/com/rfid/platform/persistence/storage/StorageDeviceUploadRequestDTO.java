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

    @Schema(description = "数据明细")
    private List<StorageDeviceUploadDetailRequestDTO> details;

    @Schema(description = "用于指示通知单操作是否完成，为 true 时，不限定 epcs 的值")
    private Boolean oprtEnd = false;

}
