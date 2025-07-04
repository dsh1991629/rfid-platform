package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户分页查询部门信息对象
 * 用于在账户分页查询中指定部门筛选条件
 */
@Data
@Schema(description = "账户分页查询部门数据传输对象")
public class AccountPageDepartmentDTO implements Serializable {
    
    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long id;
}
