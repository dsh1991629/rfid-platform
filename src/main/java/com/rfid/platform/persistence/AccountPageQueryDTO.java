package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountPageQueryDTO implements Serializable {

    private String code;

    private String name;

    private AccountPageDepartmentDTO department;

    private AccountPageRoleDTO role;
}
