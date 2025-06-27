package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class LoginRetDTO implements Serializable {

    private String account;

    private String accessToken;

    private Long expiresIn;

    private String refreshToken;

    private DepartmentDTO department;

    private RoleDTO role;

    private List<MenuDTO> menus = new ArrayList<>();



}
