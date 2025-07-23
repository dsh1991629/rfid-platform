package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "设备查询出库通知单结果数据传输对象")
public class DevOutBoundOrderQueryResponseDTO implements Serializable {

    @Schema(description = "出库通知单结果数据详情")
    private List<DevOutBoundOrderQueryDetailResponseDTO> orders = new ArrayList<>();

}
