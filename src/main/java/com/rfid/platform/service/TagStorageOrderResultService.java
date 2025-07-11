package com.rfid.platform.service;

public interface TagStorageOrderResultService {
    
    /**
     * 根据订单号和产品编码查询完成数量
     * @param orderNo 订单号
     * @param productCode 产品编码
     * @return 完成数量
     */
    int countCompletedByOrderNoAndProductCode(String orderNo, String productCode);
}
