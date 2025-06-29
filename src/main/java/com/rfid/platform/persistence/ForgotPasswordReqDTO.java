package com.rfid.platform.persistence;

import lombok.Data;

@Data
public class ForgotPasswordReqDTO {
    
    /**
     * 账号（用户名或邮箱）
     */
    private String account;
    
    /**
     * 验证码
     */
    private String captchaCode;
    
    /**
     * 验证码key
     */
    private String captchaKey;
}