package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentDTO implements Serializable {

    private Long id;

    private String name;

    private Long parentId;

    private String createDate;

    private String createAccountName;

}
