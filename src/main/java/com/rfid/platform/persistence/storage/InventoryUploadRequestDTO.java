package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "上传盘点明细请求数据传输对象")
@Data
public class InventoryUploadRequestDTO implements Serializable {

    @Schema(description = "WMS出库单号")
    private String orderNoWMS;

    @Schema(description = "仓库编号")
    private String wh;

    @Schema(description = "操作员编号")
    private String userNo;

    @Schema(description = "件数")
    private Integer quantity;

    @Schema(description = "箱数")
    private Integer boxCnt;

    @Schema(description = "盘点数据明细")
    private List<InventoryUploadDetailRequestDTO> boxDetails;

}
