package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import lombok.Data;

@Data
public class CancelStorageOrderResponseDTO implements Serializable {

    private String orderNo;

    private Long id;

}
