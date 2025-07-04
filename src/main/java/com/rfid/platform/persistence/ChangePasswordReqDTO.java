package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 修改密码请求数据传输对象
 * 用于用户修改密码时的参数传递
 */
@Data
@Schema(description = "修改密码数据传输对象")
public class ChangePasswordReqDTO {
    
    /**
     * 原密码
     */
    @Schema(description = "用户当前密码", 
            example = "oldPassword123",
            required = true,
            minLength = 6,
            maxLength = 20)
    private String oldPassword;
    
    /**
     * 新密码
     */
    @Schema(description = "用户新密码", 
            example = "newPassword123",
            required = true,
            minLength = 6,
            maxLength = 20)
    private String newPassword;
    
    /**
     * 确认新密码
     */
    @Schema(description = "确认新密码，需与新密码保持一致", 
            example = "newPassword123",
            required = true,
            minLength = 6,
            maxLength = 20)
    private String confirmPassword;
}