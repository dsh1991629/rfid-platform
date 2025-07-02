package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountDepartmentQueryDTO implements Serializable {

    private AccountPageDepartmentDTO department;
}
