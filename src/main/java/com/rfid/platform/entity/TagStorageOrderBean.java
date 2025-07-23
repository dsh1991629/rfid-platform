package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName("TAG_STORAGE_ORDER")
public class TagStorageOrderBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "ORDER_NO_WMS")
    private String orderNoWms;

    @TableField(value = "ORDER_NO_ERP")
    private String orderNoErp;

    @TableField(value = "ORDER_NO_RMS")
    private String orderNoRms;

    @TableField(value = "ORDER_TYPE")
    private String orderType;

    @TableField(value = "WH")
    private String wh;

    @TableField(value = "TYPE")
    private Integer type;

    @TableField(value = "STATE")
    private Integer state;

    @TableField(value = "CREATE_DATE")
    private String createDate;

    @TableField(value = "CREATE_USER")
    private String createUser;

    @TableField(value = "CREATE_TIME")
    private Long createTime;

    @TableField(value = "UPDATE_DATE")
    private String updateDate;

    @TableField(value = "UPDATE_USER")
    private String updateUser;

    @TableField(value = "UPDATE_TIME")
    private Long updateTime;

}
