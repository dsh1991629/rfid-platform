package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoleUpdateDTO implements Serializable {
    
    private Long id;

    private String name;

    private List<MenuDTO> menus = new ArrayList<>();

}
