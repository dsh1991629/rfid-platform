package com.rfid.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName("TAG_STORAGE_ORDER_DETAIL")
public class TagStorageOrderDetailBean implements Serializable {

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField(value = "ORDER_NO")
    private String orderNo;

    @TableField(value = "PRODUCT_CODE")
    private String productCode;

    @TableField(value = "PRODUCT_NAME")
    private String productName;

    @TableField(value = "PRODUCT_SIZE")
    private String productSize;

    @TableField(value = "PRODUCT_COLOR")
    private String productColor;

    @TableField(value = "SKU")
    private String sku;

    @TableField(value = "QUANTITY")
    private Integer quantity;

    @TableField(value = "BOX_CNT")
    private Integer boxCnt;

    @TableField(value = "BOX_CODES")
    private String boxCodes;

}
