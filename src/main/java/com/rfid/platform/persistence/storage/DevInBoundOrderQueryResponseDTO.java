package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "设备查询入库通知单结果数据传输对象")
public class DevInBoundOrderQueryResponseDTO implements Serializable {

    @Schema(description = "入库通知单结果数据详情")
    private List<DevInBoundOrderQueryDetailResponseDTO> orders = new ArrayList<>();

}
