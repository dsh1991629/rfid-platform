package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色数据传输对象
 * 用于角色信息的数据传输
 */
@Data
@Schema(description = "角色信息数据传输对象")
public class RoleDTO implements Serializable {
    
    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "管理员")
    private String name;

    /**
     * 角色别名
     */
    @Schema(description = "角色别名", example = "administrator")
    private String alias;

    /**
     * 创建日期
     */
    @Schema(description = "创建日期", example = "2023-12-01 10:30:00")
    private String createDate;

    /**
     * 创建人账户名
     */
    @Schema(description = "创建人账户名", example = "admin")
    private String createAccountName;

    /**
     * 角色关联的菜单列表
     */
    @Schema(description = "角色关联的菜单列表")
    private List<MenuDTO> menus = new ArrayList<>();

}
