package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.entity.TagStorageOrderBean;
import com.rfid.platform.persistence.storage.DevInBoundOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.DevInventoryOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.DevOutBoundOrderQueryRequestDTO;
import com.rfid.platform.persistence.storage.InBoundOrderRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderCreateRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundOrderRequestDTO;

import java.util.List;

public interface TagStorageOrderService {

    String saveInboundTagStorageOrder(String timeStamp, InBoundOrderRequestDTO inBoundOrderRequestDTO);

    String saveOutboundTagStorageOrder(String timeStamp, OutBoundOrderRequestDTO outBoundOrderRequestDTO);

    String saveInventoryTagStorageOrder(String timeStamp, InventoryOrderRequestDTO inventoryOrderRequestDTO);

    boolean checkStorageOrderCancelable(String orderNoWms, Integer type);

    boolean checkInventoryOrderCancelable(String orderNoWms, String orderNoRms, Integer type);

    String cancelTagStorageOrder(String timeStamp, String orderNoWms, Integer type);

    String cancelInventoryTagStorageOrder(String timeStamp, String orderNoWms, String orderNoRms, Integer type);

    List<TagStorageOrderBean> queryActiveInBoundOrders(DevInBoundOrderQueryRequestDTO data);

    List<TagStorageOrderBean> queryActiveOutBoundOrders(DevOutBoundOrderQueryRequestDTO data);

    List<TagStorageOrderBean> queryActiveInventoryOrders(DevInventoryOrderQueryRequestDTO data);

    boolean updateOrderStateByOrderNo(String orderNoRms, String timeStamp, Integer state);

    TagStorageOrderBean queryTagStorageOrderByNo(String orderNoRms);

    String createInventoryTagStorageOrder(String timeStamp, InventoryOrderCreateRequestDTO inventoryOrderCreateRequestDTO);

    IPage<TagStorageOrderBean> pageTagStorageOrder(Page<TagStorageOrderBean> page, LambdaQueryWrapper<TagStorageOrderBean> queryWrapper);
}
