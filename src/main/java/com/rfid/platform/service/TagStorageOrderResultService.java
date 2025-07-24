package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderResultBean;

import java.util.List;

public interface TagStorageOrderResultService {
    
    /**
     * 根据订单号和产品编码查询完成数量
     * @param orderNoRms 订单号
     * @return 完成数量
     */
    int countCompletedByOrderNoAndProductCode(String orderNoRms, String productCode);

    boolean saveStorageOrderResults(List<TagStorageOrderResultBean> resultBeans);

    List<TagStorageOrderResultBean> listTagStorageOrderResults(String orderNoRms);

    List<TagStorageOrderResultBean> listTagStorageOrderResultsByOrderRmsAndProductCode(String orderNoRms, String productCode);

    boolean existResultByBox(String boxCode);

    boolean removeStorageOrderResults(String orderNoRms, String boxCode);

    List<TagStorageOrderResultBean> listTagStorageOrderResultsByOrderRmsAndBoxCode(String orderNoRms, String boxCode);

}
