package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 设备登录返回数据传输对象
 * 用于封装设备登录成功后返回数据
 */
@Data
@Schema(description = "设备登录返回数据传输对象")
public class DeviceLoginRetDTO implements Serializable {

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

}
