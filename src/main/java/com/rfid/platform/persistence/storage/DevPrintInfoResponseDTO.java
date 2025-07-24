package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "打印信息响应数据传输对象")
@Data
public class DevPrintInfoResponseDTO implements Serializable {

    @Schema(description = "信息")
    private String printInfo;

}
