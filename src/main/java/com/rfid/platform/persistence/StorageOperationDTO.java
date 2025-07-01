package com.rfid.platform.persistence;

import java.io.Serializable;
import lombok.Data;

@Data
public class StorageOperationDTO implements Serializable {

    private String ticketNo;

    private Long quantity;

}
