package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 登录返回数据传输对象
 * 用于封装用户登录成功后返回的用户信息和权限数据
 */
@Data
@Schema(description = "WMS登录返回数据传输对象")
public class WmsLoginRetDTO implements Serializable {

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * 令牌过期时间（秒）
     */
    @Schema(description = "令牌过期时间（秒）", example = "3600")
    private Long expiresIn;

}
