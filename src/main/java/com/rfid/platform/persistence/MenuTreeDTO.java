package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树形结构数据传输对象
 * 用于表示层级菜单的树形结构
 */
@Data
@Schema(description = "菜单树形结构数据传输对象")
public class MenuTreeDTO implements Serializable {

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
    private List<MenuTreeDTO> children;

}
