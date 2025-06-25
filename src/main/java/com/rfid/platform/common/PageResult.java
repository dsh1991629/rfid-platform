package com.rfid.platform.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private Long pages = 0L;

    private Long total = 0L;

    private List<T> data = List.of();

}
