package com.rfid.platform.persistence;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库单相关DTO
 */
public class InboundDTO {

    /**
     * 入库单参数
     */
    @Data
    public static class InboundParam implements Serializable {
        /**
         * 单据编号，唯一值
         */
        private String billNo;

        /**
         * 伯俊入库单号
         */
        private String upstreamBillNo;

        /**
         * 入库时间，yyyy-MM-dd HH:mm:ss
         */
        private String receiptTime;

        /**
         * 单据明细
         */
        private List<InboundItem> itemData;
    }

    /**
     * 入库单明细
     */
    @Data
    public static class InboundItem implements Serializable {
        /**
         * SKU编码
         */
        private String skuCode;

        /**
         * 数量
         */
        private Integer qty;

        /**
         * EPC标签明细
         */
        private List<InboundMsItem> msitemData;
    }

    /**
     * EPC标签明细
     */
    @Data
    public static class InboundMsItem implements Serializable {
        /**
         * SKU编码
         */
        private String skuCode;

        /**
         * 唯一码
         */
        private String msCode;

        /**
         * EPC标签
         */
        private String epc;
    }
}