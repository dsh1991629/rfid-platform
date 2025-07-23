package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "设备查询盘点通知单结果数据传输对象")
public class DevInventoryOrderQueryResponseDTO implements Serializable {

    @Schema(description = "盘点通知单结果数据详情")
    private List<DevInventoryOrderQueryDetailResponseDTO> orders = new ArrayList<>();

}
