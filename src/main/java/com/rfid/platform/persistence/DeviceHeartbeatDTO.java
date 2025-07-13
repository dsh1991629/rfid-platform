package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "设备心跳数据传输对象")
public class DeviceHeartbeatDTO implements Serializable {

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "执行时间")
    private String createDate;

    @Schema(description = "类型")
    private String type;

}
