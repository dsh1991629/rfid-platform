package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "上传库内装箱明细请求数据传输对象")
@Data
public class WhPackDetailsRequestDTO implements Serializable {

    @Schema(description = "RMS通知单号")
    private String orderNoRms;

    @Schema(description = "仓库编码")
    private String wh;

    @Schema(description = "盘点数据明细")
    private List<WhPackUploadDetailRequestDTO> boxDetails;

}
