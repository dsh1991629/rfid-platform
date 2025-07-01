package com.rfid.platform.persistence;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 盘点单相关DTO
 */
public class InventoryDTO {

    /**
     * 查询盘点单账面明细参数
     */
    @Data
    public static class InventoryQueryParam implements Serializable {
        /**
         * 盘点单号，唯一值
         */
        private String billNo;

        /**
         * 店仓统一编码
         */
        private String storeCode;

        /**
         * 页码，默认值1
         */
        private Integer pageNo = 1;

        /**
         * 每页数量，最大值100
         */
        private Integer pageSize = 20;
    }

    /**
     * 盘点单账面明细
     */
    @Data
    public static class InventoryBookItem implements Serializable {
        /**
         * SKU编码
         */
        private String skuCode;

        /**
         * 数量
         */
        private Integer qty;
    }

    /**
     * 盘点单实际明细回传参数
     */
    @Data
    public static class InventoryParam implements Serializable {
        /**
         * 单据编号，唯一值
         */
        private String billNo;

        /**
         * 伯俊盘点单号
         */
        private String upstreamBillNo;

        /**
         * 回传时间，yyyy-MM-dd HH:mm:ss
         */
        private String receiptTime;

        /**
         * 单据明细
         */
        private List<InventoryItem> itemData;
    }

    /**
     * 盘点单明细
     */
    @Data
    public static class InventoryItem implements Serializable {
        /**
         * SKU编码
         */
        private String skuCode;

        /**
         * 实盘数量
         */
        private Integer qty;

        /**
         * EPC标签明细
         */
        private List<InventoryMsItem> msitemData;
    }

    /**
     * EPC标签明细
     */
    @Data
    public static class InventoryMsItem implements Serializable {
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