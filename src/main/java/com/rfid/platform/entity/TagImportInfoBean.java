package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;

@Data
public class TagImportInfoBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "ECP_CODE")
    private String ecpCode;

    @TableField(value = "SKU_CODE")
    private String skuCode;

    @TableField(value = "IMPORT_TYPE")
    private Integer importType;

    @TableField(value = "EXEC_NO")
    private String execNo;

    @TableField(value = "IMPORT_TIME")
    private LocalDateTime importTime;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 