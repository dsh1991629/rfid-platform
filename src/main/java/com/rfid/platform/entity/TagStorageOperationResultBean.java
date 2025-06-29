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
@TableName("TAG_STORAGE_OPERATION_RESULT")
public class TagStorageOperationResultBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "EPC_CODE")
    private String epcCode;

    @TableField(value = "SKU_CODE")
    private String skuCode;

    @TableField(value = "BILL_NO")
    private String billNo;

    @TableField(value = "NOTICE_NO")
    private String noticeNo;

    @TableField(value = "NOTICE_TYPE")
    private Integer noticeType;

    @TableField(value = "NOTICE_TIME")
    private LocalDateTime noticeTime;

    @TableField(value = "EXEC_NO")
    private String execNo;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 