package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 根据部门查询账户请求对象
 * 用于根据部门信息查询该部门下的所有账户
 */
@Data
@Schema(description = "根据部门查询账户数据传输对象")
public class AccountDepartmentQueryDTO implements Serializable {

    /**
     * 部门信息
     */
    @Schema(description = "部门信息", required = true)
    private AccountPageDepartmentDTO department;
}
