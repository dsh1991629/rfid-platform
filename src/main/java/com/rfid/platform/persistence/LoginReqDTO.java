package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求数据传输对象
 */
@Data
@Schema(description = "登录请求数据传输对象")
public class LoginReqDTO implements Serializable {

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
    @Schema(description = "验证码", example = "1234", required = true)
    private String captchaCode;

    /**
     * 验证码key
     */
    @Schema(description = "验证码唯一标识", example = "captcha_key_123", required = true)
    private String captchaKey;

}
