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
@TableName("ACCOUNT")
public class AccountBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "CODE")
    private String code;

    @TableField(value = "NAME")
    private String name;

    @TableField(value = "PASSWORD")
    private String password;

    @TableField(value = "EMAIL")
    private String email;

    @TableField(value = "PHONE")
    private String phone;

    @TableField(value = "AVATAR")
    private String avatar;

    @TableField(value = "STATE")
    private Integer state;

    @TableField(value = "LOCK_TIME")
    private LocalDateTime lockTime;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "UPDATE_ID", fill = FieldFill.UPDATE)
    private Long updateId;

    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
} 