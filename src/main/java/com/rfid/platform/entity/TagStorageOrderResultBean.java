package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
@TableName("TAG_STORAGE_ORDER_RESULT")
public class TagStorageOrderResultBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "ORDER_NO_RMS")
    private String orderNoRms;

    @TableField(value = "PRODUCT_CODE")
    private String productCode;

    @TableField(value = "BOX_CODE")
    private String boxCode;

    @TableField(value = "EPC")
    private String epc;

    @TableField(value = "CREATE_USER")
    private String createUser;

    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;
}
