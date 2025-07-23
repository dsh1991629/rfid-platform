package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "上传入库明细请求数据传输对象")
@Data
public class InBoundUploadRequestDTO implements Serializable {

    @Schema(description = "WMS入库单号")
    private String orderNoWMS;

    @Schema(description = "仓库编号")
    private String wh;

    @Schema(description = "入库数据明细")
    private List<InBoundUploadDetailRequestDTO> items;

}
