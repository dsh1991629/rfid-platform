package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.persistence.storage.StorageOrderItemRequestDTO;
import java.util.List;

public interface TagStorageOrderDetailService {

    boolean saveStorageOrderDetails(String orderNo, List<StorageOrderItemRequestDTO> items);

    List<TagStorageOrderDetailBean> listTagStorageOrderDetails(String orderNo);
}
