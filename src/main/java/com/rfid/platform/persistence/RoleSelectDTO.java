package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "角色下拉列表数据传输对象")
@Data
public class RoleSelectDTO implements Serializable {

    @Schema(description = "响应状态码", example = "1")
    private Long id;

    @Schema(description = "响应状态码", example = "超级管理员")
    private String name;

}
