package com.rfid.platform.persistence;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuUpdateDTO implements Serializable {

    private Long id;

    private String name;

}
