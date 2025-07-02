package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoleCreateDTO implements Serializable {

    private String name;

    private List<MenuDTO> menus = new ArrayList<>();

}
