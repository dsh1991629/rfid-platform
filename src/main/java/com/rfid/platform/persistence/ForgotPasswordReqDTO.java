package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "忘记密码数据传输对象")
public class ForgotPasswordReqDTO {
    
    /**
     * 账号（用户名或邮箱）
     */
    @Schema(description = "账号（用户名或邮箱）", required = true, example = "user@example.com")
    private String account;
    
    /**
     * 验证码
     */
    @Schema(description = "验证码", required = true, example = "1234")
    private String captchaCode;
    
    /**
     * 验证码key
     */
    @Schema(description = "验证码的唯一标识key", required = true, example = "captcha_key_123456")
    private String captchaKey;
}