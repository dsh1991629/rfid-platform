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
@TableName("TAG_INFO")
public class TagInfoBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "EPC")
    private String epc;

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

    @TableField(value = "STATE")
    private Integer state;

    @TableField(value = "STORAGE_STATE")
    private Integer storageState;

    @TableField(value = "IN_TIME")
    private LocalDateTime inTime;

    @TableField(value = "OUT_TIME")
    private LocalDateTime outTime;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 