package com.rfid.platform.persistence;

import java.io.Serializable;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 库存操作数据传输对象
 * 用于封装库存操作相关的数据
 */
@Data
@Schema(description = "出入库单数据传输对象")
public class StorageOperationDTO implements Serializable {

    /**
     * 票据编号
     */
    @Schema(description = "出入库单号", example = "TK202312010001")
    private String ticketNo;

    /**
     * 数量
     */
    @Schema(description = "出入库数量", example = "100")
    private Long quantity;

}
