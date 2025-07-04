package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 部门更新数据传输对象
 */
@Data
@Schema(description = "部门更新数据传输对象")
public class DepartmentUpdateDTO implements Serializable {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long id;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "技术部")
    private String name;

    /**
     * 父级部门ID
     */
    @Schema(description = "父级部门ID", example = "0")
    private Long parentId;

}
