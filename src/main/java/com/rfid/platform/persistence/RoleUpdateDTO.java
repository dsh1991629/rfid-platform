package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色更新数据传输对象
 * 用于接收角色更新请求的参数
 */
@Data
@Schema(description = "角色更新数据传输对象")
public class RoleUpdateDTO implements Serializable {
    
    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1", required = true)
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "管理员")
    private String name;

    /**
     * 角色关联的菜单列表
     */
    @Schema(description = "角色关联的菜单列表")
    private List<MenuDTO> menus = new ArrayList<>();

}
