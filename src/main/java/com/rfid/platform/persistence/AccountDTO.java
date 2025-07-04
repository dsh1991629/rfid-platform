package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户数据传输对象
 * 用于封装用户账户相关信息
 */
@Data
@Schema(description = "账户信息数据传输对象")
public class AccountDTO implements Serializable {

    @Schema(description = "账户ID", example = "1")
    private Long id;

    @Schema(description = "账户编码", example = "ACC001")
    private String code;

    @Schema(description = "账户名称", example = "张三")
    private String name;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    @Schema(description = "头像URL", example = "http://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "账户状态(0-禁用，1-启用)", example = "1", allowableValues = {"0", "1"})
    private Integer state;

    @Schema(description = "状态名称", example = "启用")
    private String stateName;

    @Schema(description = "所属部门信息")
    private DepartmentDTO department;

    @Schema(description = "角色信息")
    private RoleDTO role;

}
