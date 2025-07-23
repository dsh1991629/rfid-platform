package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 设备登录请求数据传输对象
 */
@Data
@Schema(description = "设备登录请求数据传输对象")
public class DeviceLoginReqDTO implements Serializable {

    /**
     * 用户账号
     */
    @Schema(description = "用户账号", example = "dev-admin", required = true)
    private String username;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码", example = "4b3090d8e48224c59008f21a03b859e6", required = true)
    private String password;

    /**
     * 设备类型
     */
    @Schema(description = "设备类型", example = "TDJ通道机、SCJ手持机、PB平板、DYJ打印机", required = true)
    private String devType;

    /**
     * 设备规格
     */
    @Schema(description = "设备规格", example = "QMX-100x200-V1.0", required = true)
    private String devModel;

    /**
     * 设备编码
     */
    @Schema(description = "设备编码", example = "TDJ-1x2-V1.0-250713001", required = true)
    private String devCode;

    /**
     * 仓库编码
     */
    @Schema(description = "仓库编码", example = "JJSHC-居家收货仓", required = true)
    private String wh;


}
