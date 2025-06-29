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

    @TableField(value = "EPC_CODE")
    private String epcCode;

    @TableField(value = "SKU_CODE")
    private String skuCode;

    @TableField(value = "STATE")
    private Integer state;

    @TableField(value = "IN_TIME")
    private LocalDateTime inTime;

    @TableField(value = "OUT_TIME")
    private LocalDateTime outTime;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    private LocalDateTime createTime;
} 