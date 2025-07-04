package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * 入库单相关DTO
 */
@Schema(description = "入库单明细数据传输对象")
public class InboundDTO {

    /**
     * 入库单参数
     */
    @Data
    @Schema(description = "入库单参数")
    public static class InboundParam implements Serializable {
        /**
         * 单据编号，唯一值
         */
        @Schema(description = "单据编号，唯一值", example = "IB202312010001", required = true)
        private String billNo;

        /**
         * 伯俊入库单号
         */
        @Schema(description = "伯俊入库单号", example = "BJ202312010001")
        private String upstreamBillNo;

        /**
         * 入库时间，yyyy-MM-dd HH:mm:ss
         */
        @Schema(description = "入库时间", example = "2023-12-01 10:30:00", pattern = "yyyy-MM-dd HH:mm:ss")
        private String receiptTime;

        /**
         * 单据明细
         */
        @Schema(description = "单据明细列表")
        private List<InboundItem> itemData;
    }

    /**
     * 入库单明细
     */
    @Data
    @Schema(description = "入库单明细")
    public static class InboundItem implements Serializable {
        /**
         * SKU编码
         */
        @Schema(description = "SKU编码", example = "SKU001", required = true)
        private String skuCode;

        /**
         * 数量
         */
        @Schema(description = "数量", example = "100", minimum = "1")
        private Integer qty;

        /**
         * EPC标签明细
         */
        @Schema(description = "EPC标签明细列表")
        private List<InboundMsItem> msitemData;
    }

    /**
     * EPC标签明细
     */
    @Data
    @Schema(description = "EPC标签明细")
    public static class InboundMsItem implements Serializable {
        /**
         * SKU编码
         */
        @Schema(description = "SKU编码", example = "SKU001", required = true)
        private String skuCode;

        /**
         * 唯一码
         */
        @Schema(description = "唯一码", example = "MS001", required = true)
        private String msCode;

        /**
         * EPC标签
         */
        @Schema(description = "EPC标签", example = "3000000000000000000000001", required = true)
        private String epc;
    }
}