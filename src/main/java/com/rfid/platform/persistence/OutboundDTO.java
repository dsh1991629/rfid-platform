package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 出库单相关DTO
 */
@Schema(description = "出库单明细数据传输对象")
public class OutboundDTO {

    /**
     * 出库单参数
     */
    @Data
    @Schema(description = "出库单参数")
    public static class OutboundParam implements Serializable {
        /**
         * 单据编号，唯一值
         */
        @Schema(description = "单据编号，唯一值", example = "OUT202312010001")
        private String billNo;

        /**
         * 伯俊出库单号
         */
        @Schema(description = "伯俊出库单号", example = "BJ202312010001")
        private String upstreamBillNo;

        /**
         * 出库时间，yyyy-MM-dd HH:mm:ss
         */
        @Schema(description = "出库时间", example = "2023-12-01 10:30:00", pattern = "yyyy-MM-dd HH:mm:ss")
        private String receiptTime;

        /**
         * 单据明细
         */
        @Schema(description = "单据明细列表")
        private List<OutboundItem> itemData;
    }

    /**
     * 出库单明细
     */
    @Data
    @Schema(description = "出库单明细")
    public static class OutboundItem implements Serializable {
        /**
         * SKU编码
         */
        @Schema(description = "SKU编码", example = "SKU001", required = true)
        private String skuCode;

        /**
         * 数量
         */
        @Schema(description = "数量", example = "10", minimum = "1")
        private Integer qty;

        /**
         * EPC标签明细
         */
        @Schema(description = "EPC标签明细列表")
        private List<OutboundMsItem> msitemData;
    }

    /**
     * EPC标签明细
     */
    @Data
    @Schema(description = "EPC标签明细")
    public static class OutboundMsItem implements Serializable {
        /**
         * SKU编码
         */
        @Schema(description = "SKU编码", example = "SKU001", required = true)
        private String skuCode;

        /**
         * 唯一码（12位）
         */
        @Schema(description = "唯一码（12位）", example = "123456789012", minLength = 12, maxLength = 12)
        private String msCode;

        /**
         * EPC标签
         */
        @Schema(description = "EPC标签", example = "3000123456789012345678901234")
        private String epc;
    }
}