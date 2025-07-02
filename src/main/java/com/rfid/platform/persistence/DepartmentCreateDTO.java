package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentCreateDTO implements Serializable {

    private String name;

    private Long parentId;

}
