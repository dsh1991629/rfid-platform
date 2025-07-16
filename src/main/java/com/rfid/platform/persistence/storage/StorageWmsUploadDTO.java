package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class StorageWmsUploadDTO implements Serializable {

    private String orderNo;

    private String lvNo;

    private String userNo;

    private Integer quantity;

    private Integer boxCnt;

    private List<StorageWmsUploadDetailDTO> boxDetails;

}
