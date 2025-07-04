package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户分页查询请求对象
 * 用于账户列表的分页查询和条件筛选
 */
@Data
@Schema(description = "账户分页查询数据传输对象")
public class AccountPageQueryDTO implements Serializable {

    /**
     * 账户编码
     */
    @Schema(description = "账户编码", example = "ACC001")
    private String code;

    /**
     * 账户名称
     */
    @Schema(description = "账户名称", example = "张三")
    private String name;

    /**
     * 部门信息
     */
    @Schema(description = "部门信息")
    private AccountPageDepartmentDTO department;

    /**
     * 角色信息
     */
    @Schema(description = "角色信息")
    private AccountPageRoleDTO role;
}
