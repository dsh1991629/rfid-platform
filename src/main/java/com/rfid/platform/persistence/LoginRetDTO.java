package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录返回数据传输对象
 * 用于封装用户登录成功后返回的用户信息和权限数据
 */
@Data
@Schema(description = "登录返回数据传输对象")
public class LoginRetDTO implements Serializable {

    /**
     * 用户账号
     */
    @Schema(description = "用户账号", example = "administrator")
    private String account;

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * 令牌过期时间（秒）
     */
    @Schema(description = "令牌过期时间（秒）", example = "3600")
    private Long expiresIn;

    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌", example = "refresh_token_example")
    private String refreshToken;

    /**
     * 用户所属部门信息
     */
    @Schema(description = "用户所属部门信息")
    private DepartmentDTO department;

    /**
     * 用户角色信息
     */
    @Schema(description = "用户角色信息")
    private RoleDTO role;

    /**
     * 用户可访问的菜单列表
     */
    @Schema(description = "用户可访问的菜单列表")
    private List<MenuDTO> menus = new ArrayList<>();

}
