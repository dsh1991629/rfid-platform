package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "通知单数据传输对象")
@Data
public class StorageOrderDTO implements Serializable {

    @Schema(description = "通知单ID")
    private Long id;

    @Schema(description = "RMS通知单编号")
    private String orderNoRms;

    @Schema(description = "WMS通知单编号")
    private String orderNoWms;

    @Schema(description = "ERP通知单编号")
    private String orderNoErp;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "收货仓库")
    private String wh;

    @Schema(description = "通知单类型")
    private String orderType;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "状态名称")
    private String stateName;

    @Schema(description = "创建时间")
    private String createDate;

    @Schema(description = "完成时间")
    private String finishDate;

    @Schema(description = "数量")
    private Integer quantity;



}
