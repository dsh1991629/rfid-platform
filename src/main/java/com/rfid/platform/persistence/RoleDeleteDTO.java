package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色删除数据传输对象
 * 用于角色删除操作的请求参数
 */
@Data
@Schema(description = "角色删除数据传输对象")
public class RoleDeleteDTO implements Serializable {
    
    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1", required = true)
    private Long id;

}
