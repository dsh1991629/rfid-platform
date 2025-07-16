package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class StorageWmsUploadDetailDTO implements Serializable {

    private String boxCode;

    private String productCode;

    private String sku;

    private List<String> rfids = new ArrayList<>();

}
