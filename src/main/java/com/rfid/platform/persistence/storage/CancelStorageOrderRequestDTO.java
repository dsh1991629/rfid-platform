package com.rfid.platform.persistence.storage;

import java.io.Serializable;
import lombok.Data;

@Data
public class CancelStorageOrderRequestDTO implements Serializable {

    private String orderNo;

}
