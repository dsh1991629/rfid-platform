package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuTreeDTO implements Serializable {

    private Long id;

    private String name;

    private List<MenuTreeDTO> children;

}
