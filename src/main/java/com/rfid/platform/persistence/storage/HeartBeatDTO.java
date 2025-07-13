package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "心跳数据传输对象")
@Data
public class HeartBeatDTO implements Serializable {

    @Schema(description = "设备类型")
    private String devType;

    @Schema(description = "设备型号")
    private String devModel;

    @Schema(description = "设备编码")
    private String devCode;

    @Schema(description = "设备地址")
    private String devLocation;

}
