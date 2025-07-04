package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 验证码数据传输对象
 * 用于封装验证码图片和对应的key值
 */
@Data
@Schema(description = "验证码数据传输对象")
public class CaptchaDTO implements Serializable {

    /**
     * 验证码图片的Base64编码字符串
     */
    @Schema(description = "验证码图片的Base64编码字符串", 
            example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
            required = true)
    private String image;

    /**
     * 验证码的唯一标识key
     */
    @Schema(description = "验证码的唯一标识key", 
            example = "captcha_123456789",
            required = true)
    private String key;
}
