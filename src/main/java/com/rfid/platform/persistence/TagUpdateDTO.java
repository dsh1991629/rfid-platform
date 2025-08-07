package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 标签更新数据传输对象
 */
@Data
@Schema(description = "标签更新数据传输对象")
public class TagUpdateDTO implements Serializable {

    @Schema(description = "标签ID", required = true)
    private Long id;

    /**
     * SKU编码
     */
    @Schema(description = "SKU编码")
    private String sku;

    /**
     * EPC编码
     */
    @Schema(description = "EPC编码")
    private String epc;

    /**
     * 款式码
     */
    @Schema(description = "款式码")
    private String productCode;

    /**
     * 款式名称
     */
    @Schema(description = "款式名称")
    private String productName;

    /**
     * 款式尺寸
     */
    @Schema(description = "款式尺寸")
    private String productSize;

    /**
     * 款式颜色
     */
    @Schema(description = "款式颜色")
    private String productColor;

}