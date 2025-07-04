package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 部门树形结构数据传输对象
 * 用于表示部门的层级关系和基本信息
 */
@Data
@Schema(description = "部门树形结构数据传输对象")
public class DepartmentTreeDTO implements Serializable {

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
     * 创建日期
     */
    @Schema(description = "创建日期", example = "2024-01-01")
    private String createDate;

    /**
     * 创建人账户名
     */
    @Schema(description = "创建人账户名", example = "admin")
    private String createAccountName;

    /**
     * 子部门列表
     */
    @Schema(description = "子部门列表")
    private List<DepartmentTreeDTO> children;

}
