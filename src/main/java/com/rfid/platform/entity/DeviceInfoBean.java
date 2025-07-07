package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("DEVICE_INFO")
public class DeviceInfoBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "DEVICE_NAME")
    private String deviceName;

    @TableField(value = "DEVICE_MODEL")
    private String deviceModel;

    @TableField(value = "DEVICE_CODE")
    private String deviceCode;

    @TableField(value = "DEVICE_LOCATION")
    private String deviceLocation;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
