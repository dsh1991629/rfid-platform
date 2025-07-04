package com.rfid.platform.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "分页数据通用传输数据对象")
public class PageResult<T> implements Serializable {

    @Schema(description = "当前页码")
    private Integer pageNum = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 10;

    @Schema(description = "总页数")
    private Long pages = 0L;

    @Schema(description = "总数")
    private Long total = 0L;

    @Schema(description = "响应数据集合")
    private List<T> data = List.of();

}
