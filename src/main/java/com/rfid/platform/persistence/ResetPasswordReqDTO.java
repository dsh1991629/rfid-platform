package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "重置密码请求数据对象")
public class ResetPasswordReqDTO {
    
    /**
     * 重置密码token
     */
    @Schema(description = "重置密码token", required = true, example = "abc123def456")
    private String resetToken;
    
    /**
     * 新密码
     */
    @Schema(description = "新密码", required = true, minLength = 6, maxLength = 20, example = "newPassword123")
    private String newPassword;
    
    /**
     * 确认新密码
     */
    @Schema(description = "确认新密码", required = true, minLength = 6, maxLength = 20, example = "newPassword123")
    private String confirmPassword;
}