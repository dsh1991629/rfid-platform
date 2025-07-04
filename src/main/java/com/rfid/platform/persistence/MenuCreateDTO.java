package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单创建数据传输对象
 * 用于接收创建菜单时的请求参数
 */
@Data
@Schema(description = "菜单创建数据传输对象")
public class MenuCreateDTO implements Serializable {

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "系统管理", required = true)
    private String name;

    /**
     * 父级菜单ID
     */
    @Schema(description = "父级菜单ID", example = "1")
    private Long parentId;

}
