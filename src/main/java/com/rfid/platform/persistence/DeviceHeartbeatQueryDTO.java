package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "设备心跳数据传输对象")
@Data
public class DeviceHeartbeatQueryDTO implements Serializable {

    @Schema(description = "设备编码", required = true)
    private String deviceCode;

}
