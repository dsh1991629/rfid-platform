package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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

    @TableField(value = "LOGIN_TIME")
    private LocalDateTime loginTime;

    @TableField(value = "HEARTBEAT_TIME")
    private LocalDateTime heartbeatTime;

    @TableField(value = "LOGOUT_TIME")
    private LocalDateTime logoutTime;


}
