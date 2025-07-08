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
    @Schema(description = "用户账号", example = "admin", required = true)
    private String account;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码", example = "123456", required = true)
    private String password;

    /**
     * 验证码
     */
    @Schema(description = "设备编码", example = "1234", required = true)
    private String deviceCode;


}
