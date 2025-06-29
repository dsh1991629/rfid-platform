package com.rfid.platform.persistence;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TagImportExcelDTO {
    
    @ExcelProperty(index = 0, value = "SKU编码")
    private String skuCode;
    
    @ExcelProperty(index = 1, value = "EPC编码")
    private String epcCode;
}