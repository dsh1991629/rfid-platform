package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Data
@Schema(description = "部门数据传输对象")
public class DepartmentDTO implements Serializable {

    @Schema(description = "部门ID", example = "1")
    private Long id;

    @Schema(description = "部门名称", example = "技术部")
    private String name;

    @Schema(description = "父级部门ID", example = "0")
    private Long parentId;

    @Schema(description = "创建日期", example = "2024-01-01 10:00:00")
    private String createDate;

    @Schema(description = "创建人账户名", example = "admin")
    private String createAccountName;

}
