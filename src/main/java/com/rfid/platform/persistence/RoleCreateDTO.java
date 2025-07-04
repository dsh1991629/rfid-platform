package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "角色创建数据传输对象")
public class RoleCreateDTO implements Serializable {

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "管理员", required = true)
    private String name;

    /**
     * 角色关联的菜单列表
     */
    @Schema(description = "角色关联的菜单列表")
    private List<MenuDTO> menus = new ArrayList<>();

}
