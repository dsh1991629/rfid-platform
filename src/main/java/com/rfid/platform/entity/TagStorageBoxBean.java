package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName("TAG_STORAGE_BOX")
public class TagStorageBoxBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "ORDER_NO_RMS")
    private String orderNoRms;

    @TableField(value = "BOX_CODE")
    private String boxCode;

    @TableField(value = "BOX_IDX")
    private Integer boxIdx;

}
