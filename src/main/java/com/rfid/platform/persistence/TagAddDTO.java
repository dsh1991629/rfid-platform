package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 标签导入Excel数据传输对象
 * 用于从Excel文件中导入RFID标签数据
 */
@Data
@Schema(description = "标签创建数据传输对象")
public class TagAddDTO implements Serializable {
    
    /**
     * SKU编码
     */
    @Schema(description = "SKU编码", example = "SKU001", required = true)
    private String sku;

    /**
     * EPC编码
     */
    @Schema(description = "EPC编码", example = "3000000000000000000000001", required = true)
    private String epc;

    /**
     * 款式码
     */
    @Schema(description = "款式码", example = "PD001", required = true)
    private String productCode;

    /**
     * 款式名称
     */
    @Schema(description = "款式名称", example = "衬衫", required = false)
    private String productName;

    /**
     * 款式尺寸
     */
    @Schema(description = "款式尺寸", example = "XXL", required = false)
    private String productSize;

    /**
     * 款式颜色
     */
    @Schema(description = "款式颜色", example = "红色", required = false)
    private String productColor;

}