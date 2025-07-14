package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.persistence.storage.StorageOrderItemRequestDTO;
import java.util.List;

public interface TagStorageOrderDetailService {

    boolean saveStorageOrderDetails(String orderNo, List<StorageOrderItemRequestDTO> items);

    List<TagStorageOrderDetailBean> listTagStorageOrderDetails(String orderNo, String productCode);
    
    /**
     * 根据订单号查询所有不重复的产品编码
     * @param orderNo 订单号
     * @return 不重复的产品编码列表
     */
    List<String> listDistinctProductCodes(String orderNo);

    boolean productCodeExistInOrderNo(String orderNo, String productCode);

    Integer getQuantityFromTagStorageOrderDetails(String orderNo, String productCode);
}
