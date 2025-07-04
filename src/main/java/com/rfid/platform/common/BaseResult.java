package com.rfid.platform.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "通用响应数据传输对象")
public class BaseResult<T> implements Serializable {

    @Schema(description = "响应编码 00-成功 99-失败")
    private String code;

    @Schema(description = "响应消息")
    private String message;

    /**
     * 业务数据
     */
    @Schema(description = "响应数据传输对象")
    private T data;


    public BaseResult() {
        this.code = PlatformConstant.RET_CODE.SUCCESS;
        this.message = "";
    }

}
