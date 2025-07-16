package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.persistence.storage.StorageOrderItemRequestDTO;
import java.util.List;

public interface TagStorageOrderDetailService {

    boolean saveStorageOrderDetails(String orderNo, List<StorageOrderItemRequestDTO> items);

    List<TagStorageOrderDetailBean> listTagStorageOrderDetails(String orderNo);

    boolean productCodeExistInOrderNo(String orderNo, String productCode);

    Integer getQuantityFromTagStorageOrderDetails(String orderNo, String productCode);

    List<TagStorageOrderDetailBean> listTagStorageOrderDetailsByNoAndProductCode(String orderNo, String productCode);
}
