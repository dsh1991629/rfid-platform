package com.rfid.platform.persistence;

import lombok.Data;

@Data
public class ResetPasswordReqDTO {
    
    /**
     * 重置密码token
     */
    private String resetToken;
    
    /**
     * 新密码
     */
    private String newPassword;
    
    /**
     * 确认新密码
     */
    private String confirmPassword;
}