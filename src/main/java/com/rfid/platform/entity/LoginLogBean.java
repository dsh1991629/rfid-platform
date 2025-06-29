package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("LOGIN_LOG")
public class LoginLogBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "ACCOUNT_ID")
    private Long accountId;

    @TableField(value = "ACCOUNT_CODE")
    private String accountCode;

    @TableField(value = "LOGIN_TIME")
    private LocalDateTime loginTime;

    @TableField(value = "LOGIN_STATUS")
    private String loginStatus;

    @TableField(value = "LOGOUT_TIME")
    private LocalDateTime logoutTime;

    @TableField(value = "ERROR_MSG")
    private String errorMsg;

    @TableField(value = "ACCESS_TOKEN")
    private String accessToken;

}
