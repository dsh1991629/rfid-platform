package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 登录请求数据传输对象
 */
@Data
@Schema(description = "登录请求数据传输对象")
public class WmsLoginReqDTO implements Serializable {

    /**
     * 用户账号
     */
    @Schema(description = "登录账号", example = "admin", required = true)
    private String appId;

    /**
     * 用户密码
     */
    @Schema(description = "登录密码, MD5密文", example = "dadsmajfadf82213", required = true)
    private String appSecret;

}
