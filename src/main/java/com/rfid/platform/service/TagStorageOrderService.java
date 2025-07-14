package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.persistence.storage.StorageCheckQueryRequestDTO;
import com.rfid.platform.persistence.storage.StorageOrderItemRequestDTO;
import java.util.List;

public interface TagStorageOrderService {

    Long saveInboundTagStorageOrder(String timeStamp, String orderNo, String orderType, List<StorageOrderItemRequestDTO> items);

    Long saveOutboundTagStorageOrder(String timeStamp, String orderNo, String orderType, List<StorageOrderItemRequestDTO> items);

    Long saveInventoryTagStorageOrder(String timeStamp, String orderNo, List<StorageOrderItemRequestDTO> items);

    boolean checkStorageOrderCancelable(String orderNo, Integer orderType);

    Long cancelTagStorageOrder(String timeStamp, String orderNo, Integer orderType);

    List<TagStorageOrderBean> queryActiveInBoundOrders(StorageCheckQueryRequestDTO data);

    List<TagStorageOrderBean> queryActiveOutBoundOrders(StorageCheckQueryRequestDTO data);

    List<TagStorageOrderBean> queryActiveInventoryOrders(StorageCheckQueryRequestDTO data);

    boolean updateOrderStateByOrderNo(String orderNo, String timeStamp, Integer state);
}
