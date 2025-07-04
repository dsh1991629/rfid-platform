package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 账户更新数据传输对象
 * 用于账户信息更新操作的数据传输
 */
@Data
@Schema(description = "账户更新数据传输对象")
public class AccountUpdateDTO implements Serializable {

    /**
     * 账户ID
     */
    @Schema(description = "账户ID", example = "1")
    private Long id;

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
     * 账户密码
     */
    @Schema(description = "账户密码", example = "123456")
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
     * 头像URL
     */
    @Schema(description = "头像URL", example = "http://example.com/avatar.jpg")
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
