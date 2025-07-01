package com.rfid.platform.persistence;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 出库单相关DTO
 */
public class OutboundDTO {

    /**
     * 出库单参数
     */
    @Data
    public static class OutboundParam implements Serializable {
        /**
         * 单据编号，唯一值
         */
        private String billNo;

        /**
         * 伯俊出库单号
         */
        private String upstreamBillNo;

        /**
         * 出库时间，yyyy-MM-dd HH:mm:ss
         */
        private String receiptTime;

        /**
         * 单据明细
         */
        private List<OutboundItem> itemData;
    }

    /**
     * 出库单明细
     */
    @Data
    public static class OutboundItem implements Serializable {
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
        private List<OutboundMsItem> msitemData;
    }

    /**
     * EPC标签明细
     */
    @Data
    public static class OutboundMsItem implements Serializable {
        /**
         * SKU编码
         */
        private String skuCode;

        /**
         * 唯一码（12位）
         */
        private String msCode;

        /**
         * EPC标签
         */
        private String epc;
    }
}