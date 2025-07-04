package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户创建数据传输对象
 * 用于接收创建新账户时的请求参数
 */
@Data
@Schema(description = "账户创建数据传输对象")
public class AccountCreateDTO implements Serializable {

    /**
     * 账户编码
     */
    @Schema(description = "账户编码", example = "administrator", required = true)
    private String code;

    /**
     * 账户名称
     */
    @Schema(description = "账户名称", example = "张三", required = true)
    private String name;

    /**
     * 账户密码
     */
    @Schema(description = "账户密码", example = "123456", required = true)
    private String password;

    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址", example = "http://example.com/avatar.jpg")
    private String avatar;

    /**
     * 所属部门信息
     */
    @Schema(description = "所属部门信息")
    private AccountPageDepartmentDTO department;

    /**
     * 角色信息
     */
    @Schema(description = "角色信息")
    private AccountPageRoleDTO role;

}
