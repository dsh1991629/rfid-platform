package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 部门创建数据传输对象
 */
@Data
@Schema(description = "部门创建数据传输对象")
public class DepartmentCreateDTO implements Serializable {

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "技术部", required = true)
    private String name;

    /**
     * 父级部门ID
     */
    @Schema(description = "父级部门ID，为空时表示顶级部门", example = "1")
    private Long parentId;

}
