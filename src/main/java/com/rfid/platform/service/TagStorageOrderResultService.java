package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderResultBean;

import java.util.List;

public interface TagStorageOrderResultService {
    
    /**
     * 根据订单号和产品编码查询完成数量
     * @param orderNo 订单号
     * @return 完成数量
     */
    int countCompletedByOrderNoAndProductCode(String orderNo, String productCode);

    boolean saveStorageOrderResults(List<TagStorageOrderResultBean> resultBeans);

    Integer countCompletedBoxByOrderNo(String orderNo);

    Integer countCompletedRfidByOrderNo(String orderNo);

    List<TagStorageOrderResultBean> listTagStorageOrderResults(String orderNo);
}
