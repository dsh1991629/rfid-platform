package com.rfid.platform.service;

import com.rfid.platform.entity.TagStorageOrderDetailBean;
import com.rfid.platform.persistence.storage.InBoundOrderItemRequestDTO;
import com.rfid.platform.persistence.storage.InventoryOrderItemRequestDTO;
import com.rfid.platform.persistence.storage.OutBoundOrderItemRequestDTO;
import java.util.List;

public interface TagStorageOrderDetailService {

    boolean saveInBoundOrderDetails(String orderNoRms, List<InBoundOrderItemRequestDTO> items);

    boolean saveOutBoundOrderDetails(String orderNoRms, List<OutBoundOrderItemRequestDTO> items);

    boolean saveInventoryOrderDetails(String orderNoRms, List<InventoryOrderItemRequestDTO> items);


    List<TagStorageOrderDetailBean> listTagStorageOrderDetails(String orderNoRms);

    boolean productCodeExistInOrderNo(String orderNo, String productCode);

    TagStorageOrderDetailBean getSkuByOrderNoRmsAndProductCode(String orderNoRms, String productCode);

    Integer getQuantityFromTagStorageOrderDetails(String orderNo, String productCode);

    List<TagStorageOrderDetailBean> listTagStorageOrderDetailsByNoAndProductCode(String orderNo, String productCode);
}
