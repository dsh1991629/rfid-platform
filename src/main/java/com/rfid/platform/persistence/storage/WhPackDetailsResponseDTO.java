package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "库内装箱明细响应数据传输对象")
@Data
public class WhPackDetailsResponseDTO implements Serializable {

    @Schema(description = "RMS通知单号")
    private String orderNoRMS;

}
