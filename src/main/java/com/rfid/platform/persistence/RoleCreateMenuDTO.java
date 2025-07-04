package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色创建菜单数据传输对象
 * 用于角色创建时的菜单权限配置
 */
@Data
@Schema(description = "角色创建菜单数据传输对象")
public class RoleCreateMenuDTO implements Serializable {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", example = "1")
    private Long id;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "系统管理")
    private String name;

    /**
     * 子菜单列表
     */
    @Schema(description = "子菜单列表")
    private List<RoleCreateMenuDTO> children = new ArrayList<>();

}
