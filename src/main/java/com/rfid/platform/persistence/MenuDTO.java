package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "菜单数据传输对象")
public class MenuDTO implements Serializable {

    @Schema(description = "菜单ID", example = "1")
    private Long id;

    @Schema(description = "菜单名称", example = "系统管理")
    private String name;

    @Schema(description = "菜单编码", example = "SYSTEM_MANAGE")
    private String code;

    @Schema(description = "父级菜单ID", example = "0")
    private Long parentId;
    
    @Schema(description = "菜单优先级", example = "1")
    private Integer priority; // 菜单优先级字段

    @Schema(description = "创建日期", example = "2023-12-01 10:30:00")
    private String createDate;

    @Schema(description = "创建人账户名", example = "admin")
    private String createAccountName;
    
    @Schema(description = "子菜单列表")
    private List<MenuDTO> children;
}
