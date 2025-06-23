package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;

@Data
public class TagInterfaceLogBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "TYPE")
    private Integer type;

    @TableField(value = "EXEC_NO")
    private String execNo;

    @TableField(value = "REQ_CONTENT")
    private String reqContent;

    @TableField(value = "RESP_CONTENT")
    private String respContent;

    @TableField(value = "CREATE_ID", fill = FieldFill.INSERT)
    private Long createId;

    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 