package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class StorageOrderItemRequestDTO implements Serializable {

    private String productCode;

    private String productName;

    private String productSize;

    private String productColor;

    private String sku;

    private Integer quantity;

    private Integer boxCnt;

    private List<String> boxCodes;


}
