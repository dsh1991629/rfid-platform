package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoleDTO implements Serializable {
    
    private Long id;

    private String name;

    private String createDate;

    private String createAccountName;

    private List<MenuDTO> menus = new ArrayList<>();

}
