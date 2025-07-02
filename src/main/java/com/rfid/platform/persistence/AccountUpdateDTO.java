package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountUpdateDTO implements Serializable {

    private Long id;

    private String code;

    private String name;

    private String password;

    private String email;

    private String phone;

    private String avatar;

    private AccountPageDepartmentDTO department;

    private AccountPageRoleDTO role;

}
