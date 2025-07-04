package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * 盘点单相关DTO
 */
@Schema(description = "盘点单数据传输对象")
public class InventoryDTO {

    /**
     * 查询盘点单账面明细参数
     */
    @Data
    @Schema(description = "查询盘点单账面明细参数")
    public static class InventoryQueryParam implements Serializable {
        /**
         * 盘点单号，唯一值
         */
        @Schema(description = "盘点单号，唯一值", example = "PD202312010001")
        private String billNo;

        /**
         * 店仓统一编码
         */
        @Schema(description = "店仓统一编码", example = "STORE001")
        private String storeCode;

        /**
         * 页码，默认值1
         */
        @Schema(description = "页码，默认值1", example = "1", defaultValue = "1")
        private Integer pageNo = 1;

        /**
         * 每页数量，最大值100
         */
        @Schema(description = "每页数量，最大值100", example = "20", defaultValue = "20", maximum = "100")
        private Integer pageSize = 20;
    }

    /**
     * 盘点单账面明细
     */
    @Data
    @Schema(description = "盘点单账面明细")
    public static class InventoryBookItem implements Serializable {
        /**
         * SKU编码
         */
        @Schema(description = "SKU编码", example = "SKU001", required = true)
        private String skuCode;

        /**
         * 数量
         */
        @Schema(description = "数量", example = "100", required = true)
        private Integer qty;
    }

    /**
     * 盘点单实际明细回传参数
     */
    @Data
    @Schema(description = "盘点单实际明细回传参数")
    public static class InventoryParam implements Serializable {
        /**
         * 单据编号，唯一值
         */
        @Schema(description = "单据编号，唯一值", example = "PD202312010001", required = true)
        private String billNo;

        /**
         * 伯俊盘点单号
         */
        @Schema(description = "伯俊盘点单号", example = "BJ202312010001")
        private String upstreamBillNo;

        /**
         * 回传时间，yyyy-MM-dd HH:mm:ss
         */
        @Schema(description = "回传时间，格式：yyyy-MM-dd HH:mm:ss", example = "2023-12-01 10:30:00", required = true)
        private String receiptTime;

        /**
         * 单据明细
         */
        @Schema(description = "单据明细列表", required = true)
        private List<InventoryItem> itemData;
    }

    /**
     * 盘点单明细
     */
    @Data
    @Schema(description = "盘点单明细")
    public static class InventoryItem implements Serializable {
        /**
         * SKU编码
         */
        @Schema(description = "SKU编码", example = "SKU001", required = true)
        private String skuCode;

        /**
         * 实盘数量
         */
        @Schema(description = "实盘数量", example = "95", required = true)
        private Integer qty;

        /**
         * EPC标签明细
         */
        @Schema(description = "EPC标签明细列表")
        private List<InventoryMsItem> msitemData;
    }

    /**
     * EPC标签明细
     */
    @Data
    @Schema(description = "EPC标签明细")
    public static class InventoryMsItem implements Serializable {
        /**
         * SKU编码
         */
        @Schema(description = "SKU编码", example = "SKU001", required = true)
        private String skuCode;

        /**
         * 唯一码（12位）
         */
        @Schema(description = "唯一码（12位）", example = "123456789012", required = true, minLength = 12, maxLength = 12)
        private String msCode;

        /**
         * EPC标签
         */
        @Schema(description = "EPC标签", example = "E2001234567890123456789012345678", required = true)
        private String epc;
    }
}