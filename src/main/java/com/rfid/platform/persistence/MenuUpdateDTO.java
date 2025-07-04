package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单更新数据传输对象
 * 用于接收菜单更新请求的参数
 */
@Data
@Schema(description = "菜单更新数据传输对象")
public class MenuUpdateDTO implements Serializable {

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

}
