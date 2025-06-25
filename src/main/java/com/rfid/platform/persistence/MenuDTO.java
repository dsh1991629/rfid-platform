package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuDTO implements Serializable {

    private Long id;

    private String name;

    private String code;

    private Long parentId;

    private String createDate;

    private String createAccountName;
}
