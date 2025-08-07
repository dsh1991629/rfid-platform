package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "标签数据传输对象")
@Data
public class TagDTO implements Serializable {

    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "SKU编码")
    private String sku;

    @Schema(description = "款式码")
    private String productCode;

    @Schema(description = "标签RFID码")
    private String epc;

    @Schema(description = "款式名称")
    private String productName;

    @Schema(description = "款式尺寸")
    private String productSize;

    @Schema(description = "款式颜色")
    private String productColor;

    @Schema(description = "状态")
    private Integer storageState;

    @Schema(description = "状态名称")
    private String storageStateName;

}
