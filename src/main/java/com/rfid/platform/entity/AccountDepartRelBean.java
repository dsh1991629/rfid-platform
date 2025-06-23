package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;

@Data
public class AccountDepartRelBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "ACCOUNT_ID")
    private Long accountId;

    @TableField(value = "DEPARTMENT_ID")
    private Long departmentId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;
} 