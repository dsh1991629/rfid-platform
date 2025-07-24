package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "创建箱请求数据传输对象")
@Data
public class DevAddBoxRequestDTO implements Serializable {

    @Schema(description = "RMS单号")
    private String orderID_RMS;

    @Schema(description = "箱数量")
    private Integer boxCnt;

}
