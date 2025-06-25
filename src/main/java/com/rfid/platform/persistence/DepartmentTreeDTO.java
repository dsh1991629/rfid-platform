package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DepartmentTreeDTO implements Serializable {

    private Long id;

    private String name;

    private String createDate;

    private String createAccountName;

    private List<DepartmentTreeDTO> children;

}
