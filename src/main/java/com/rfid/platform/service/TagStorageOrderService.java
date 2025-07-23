package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.persistence.storage.InBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.StorageCheckQueryRequestDTO;
import com.rfid.platform.persistence.storage.InBoundOrderItemRequestDTO;
import java.util.List;

public interface TagStorageOrderService {

    String saveInboundTagStorageOrder(String timeStamp, InBoundOrderRequestDTO inBoundOrderRequestDTO);

    String saveOutboundTagStorageOrder(String timeStamp, OutBoundOrderRequestDTO outBoundOrderRequestDTO);

    String saveInventoryTagStorageOrder(String timeStamp, InventoryOrderRequestDTO inventoryOrderRequestDTO);

    boolean checkStorageOrderCancelable(String orderNoWms, Integer type);

    boolean checkInventoryOrderCancelable(String orderNoWms, String orderNoRms, Integer type);

    String cancelTagStorageOrder(String timeStamp, String orderNoWms, Integer type);

    String cancelInventoryTagStorageOrder(String timeStamp, String orderNoWms, String orderNoRms, Integer type);

    List<TagStorageOrderBean> queryActiveInBoundOrders(StorageCheckQueryRequestDTO data);

    List<TagStorageOrderBean> queryActiveOutBoundOrders(StorageCheckQueryRequestDTO data);

    List<TagStorageOrderBean> queryActiveInventoryOrders(StorageCheckQueryRequestDTO data);

    boolean updateOrderStateByOrderNo(String orderNo, String timeStamp, Integer state);

    TagStorageOrderBean queryTagStorageOrderByNo(String orderNoRms);



}
