package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 选择数据传输对象
 * 用于封装下拉选择框等场景的基础数据
 */
@Data
@Schema(description = "选择下拉通用数据传输对象")
public class SelectDTO implements Serializable {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", example = "示例名称")
    private String name;

}
