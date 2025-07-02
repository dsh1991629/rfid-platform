package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuCreateDTO implements Serializable {

    private String name;

    private Long parentId;

}
