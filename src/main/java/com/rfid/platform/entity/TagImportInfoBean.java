package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;

@Data
@TableName("TAG_IMPORT_INFO")
public class TagImportInfoBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "EPC")
    private String epc;

    @TableField(value = "SKU_INDEX")
    private String skuIndex;

    @TableField(value = "SKU")
    private String sku;

    @TableField(value = "PRODUCT_CODE")
    private String productCode;

    @TableField(value = "PRODUCT_NAME")
    private String productName;

    @TableField(value = "PRODUCT_SIZE")
    private String productSize;

    @TableField(value = "PRODUCT_COLOR")
    private String productColor;

    @TableField(value = "IMPORT_TYPE")
    private Integer importType;

    @TableField(value = "IMPORT_RESULT")
    private String importResult;

    @TableField(value = "EXEC_NO")
    private String execNo;

    @TableField(value = "IMPORT_TIME")
    private LocalDateTime importTime;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 