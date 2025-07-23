package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "出库数据明细数据传输对象")
@Data
public class OutBoundUploadDetailRequestDTO implements Serializable {

    @Schema(description = "箱外码")
    private String boxCode;

    @Schema(description = "箱内商品明细")
    private List<OutBoundUploadDetailItemRequestDTO> boxItems;

}
