package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginReqDTO implements Serializable {

    private String account;

    private String password;

    private String captchaCode;

    private String captchaKey;

}
