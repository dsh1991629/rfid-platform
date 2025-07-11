package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class StorageOrderRequestDTO implements Serializable {

    private String orderNo;

    private List<StorageOrderItemRequestDTO> items;

}
