package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "更新箱内明细请求数据传输对象")
@Data
public class DevUpBoxRequestDTO implements Serializable {

    @Schema(description = "箱外码")
    private String boxCode;

    @Schema(description = "更新类型：Add=增加，Replace=覆盖")
    private String upType;

    @Schema(description = "箱内明细")
    private List<DevUpBoxDetailRequestDTO> details;


}
