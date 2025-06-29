package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuDTO implements Serializable {

    private Long id;

    private String name;

    private String code;

    private Long parentId;
    
    private Integer priority; // 添加优先级字段

    private String createDate;

    private String createAccountName;
    
    private List<MenuDTO> children;
}
