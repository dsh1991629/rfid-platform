package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单删除数据传输对象
 * 用于接收删除菜单操作的请求参数
 */
@Data
@Schema(description = "菜单删除数据传输对象")
public class MenuDeleteDTO implements Serializable {

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", example = "1", required = true)
    private Long id;

}
