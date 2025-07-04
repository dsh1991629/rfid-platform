package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 部门删除数据传输对象
 * 用于接收删除部门操作的请求参数
 */
@Data
@Schema(description = "部门删除数据传输对象")
public class DepartmentDeleteDTO implements Serializable {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1", required = true)
    private Long id;

}
