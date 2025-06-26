package com.rfid.platform.persistence;

import java.io.Serializable;
import lombok.Data;

@Data
public class AccountDTO implements Serializable {

    private Long id;

    private String code;

    private String name;

    private String password;

    private String email;

    private String phone;

    private String avatar;

    private Integer state;

    private String stateName;

}
