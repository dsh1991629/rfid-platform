package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "盘点通知单明细进度响应数据传输对象")
@Data
public class DevInventoryOrderQueryDetailItemProgressResponseDTO implements Serializable {

    @Schema(description = "已完成进度")
    private Integer qty;

    @Schema(description = "已扫描箱数量")
    private Integer boxCnt;

    @Schema(description = "已扫描箱码")
    private List<String> boxCodes;

}
