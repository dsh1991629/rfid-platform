package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "通知单结果详情项数据传输对象")
public class StorageCheckQueryOrderDetailItemDTO implements Serializable {

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "SKU编码")
    private String sku;

    @Schema(description = "总件数")
    private Integer totalCount;

    @Schema(description = "完成数")
    private Integer progress;

    @Schema(description = "箱数量")
    private Integer boxCnt;

    @Schema(description = "箱码")
    private List<String> boxCodes;

}
