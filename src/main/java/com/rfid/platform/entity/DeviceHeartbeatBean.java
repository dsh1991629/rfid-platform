package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName("DEVICE_HEARTBEAT")
public class DeviceHeartbeatBean implements Serializable {

    @TableId(value = "ID")
    private String id;

    @TableField(value = "DEVICE_CODE")
    private String deviceCode;

    @TableField(value = "ACCESS_TOKEN")
    private String accessToken;

    @TableField(value = "TYPE")
    private Integer type;

    @TableField(value = "CREATE_DATE")
    private String createDate;

    @TableField(value = "CREATE_TIME")
    private Long createTime;

    @TableField(value = "DEVICE_TYPE")
    private String deviceType;

    @TableField(value = "DEVICE_MODEL")
    private String deviceModel;

    @TableField(value = "DEVICE_LOCATION")
    private String deviceLocation;

}
