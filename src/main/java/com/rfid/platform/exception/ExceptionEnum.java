package com.rfid.platform.exception;

public enum ExceptionEnum {

    AUTHENTICATION_FAILED("401", "权限不足");


    private String code;
    private String des;

    ExceptionEnum(String code, String des) {
        this.code = code;
        this.des = des;
    }

    public String getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }
}
