package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "设备分页查询数据传输对象")
@Data
public class DevicePageQueryDTO implements Serializable {

    /**
     * 设备名称
     */
    @Schema(description = "设备类型", example = "通道机", required = true)
    private String deviceType;

    /**
     * 设备类型
     */
    @Schema(description = "设备型号", example = "QMX-通道机-红豆-V1", required = true)
    private String deviceModel;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码", example = "250706001", required = true)
    private String deviceCode;

}
