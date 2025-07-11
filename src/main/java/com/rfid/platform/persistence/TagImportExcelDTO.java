package com.rfid.platform.persistence;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 标签导入Excel数据传输对象
 * 用于从Excel文件中导入RFID标签数据
 */
@Data
@Schema(description = "标签推送数据传输对象")
public class TagImportExcelDTO {
    
    /**
     * SKU编码
     */
    @ExcelProperty(index = 0, value = "SKU编码")
    @Schema(description = "SKU编码", example = "SKU001", required = true)
    private String sku;

    /**
     * EPC编码
     */
    @ExcelProperty(index = 1, value = "EPC编码")
    @Schema(description = "EPC编码", example = "3000000000000000000000001", required = true)
    private String epc;

    /**
     * 款式码
     */
    @ExcelProperty(index = 2, value = "款式码")
    @Schema(description = "款式码", example = "SKU001", required = true)
    private String productCode;

}