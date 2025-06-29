package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

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

    private DepartmentDTO department;

    private RoleDTO role;

}
