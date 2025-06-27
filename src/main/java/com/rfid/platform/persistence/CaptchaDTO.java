package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class CaptchaDTO implements Serializable {

    private String image;

    private String key;
}
