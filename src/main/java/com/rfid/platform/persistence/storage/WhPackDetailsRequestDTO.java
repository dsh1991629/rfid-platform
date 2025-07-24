package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "上传库内装箱明细请求数据传输对象")
@Data
public class WhPackDetailsRequestDTO implements Serializable {

    @Schema(description = "WMS通知单号")
    private String orderNoWMS;

    @Schema(description = "仓库编码")
    private String wh;

    @Schema(description = "箱外码")
    private Integer boxCode;

    @Schema(description = "货物当前库位")
    private String binLocation;

    @Schema(description = "盘点数据明细")
    private List<WhPackUploadDetailRequestDTO> boxDetails;

}
