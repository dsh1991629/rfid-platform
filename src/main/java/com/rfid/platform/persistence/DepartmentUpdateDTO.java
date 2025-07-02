package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentUpdateDTO implements Serializable {

    private Long id;

    private String name;

    private Long parentId;

}
