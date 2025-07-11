package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "设备查询通知单结果数据传输对象")
public class StorageCheckQueryResponseDTO implements Serializable {

    @Schema(description = "通知单结果数据详情")
    private List<StorageCheckQueryOrderDetailDTO> orders = new ArrayList<>();

}
