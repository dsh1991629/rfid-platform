package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "设备创建数据传输对象")
@Data
public class DeviceCreateDTO implements Serializable {

    /**
     * 设备名称
     */
    @Schema(description = "设备名称", example = "通道机", required = true)
    private String deviceName;

    /**
     * 设备类型
     */
    @Schema(description = "设备类型", example = "QMX-通道机-红豆-V1", required = true)
    private String deviceModel;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码", example = "250706001", required = true)
    private String deviceCode;

    /**
     * 设备地址
     */
    @Schema(description = "设备地址", example = "仓库1-1楼-收货口")
    private String deviceLocation;

}
