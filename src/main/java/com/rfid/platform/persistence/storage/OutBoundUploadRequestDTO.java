package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "上传出库明细请求数据传输对象")
@Data
public class OutBoundUploadRequestDTO implements Serializable {

    @Schema(description = "WMS出库单号")
    private String orderNoWMS;

    @Schema(description = "仓库编号")
    private String wh;

    @Schema(description = "出库数据明细")
    private List<OutBoundUploadDetailRequestDTO> items;

}
