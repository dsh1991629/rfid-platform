package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户分页查询角色信息对象
 * 用于在账户分页查询中指定角色筛选条件
 */
@Data
@Schema(description = "账户分页查询角色信息数据传输对象")
public class AccountPageRoleDTO implements Serializable {
    
    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "1")
    private Long id;
}
