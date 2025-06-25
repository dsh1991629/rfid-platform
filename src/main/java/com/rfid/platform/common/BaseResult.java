package com.rfid.platform.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResult<T> implements Serializable {

    private String code;

    private String message;

    /**
     * 业务数据
     */
    private T data;


    public BaseResult() {
        this.code = PlatformConstant.RET_CODE.SUCCESS;
        this.message = "";
    }

}
