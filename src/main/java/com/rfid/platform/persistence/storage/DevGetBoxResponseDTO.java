package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "查询箱内明细响应数据传输对象")
@Data
public class DevGetBoxResponseDTO implements Serializable {

    @Schema(description = "箱内明细")
    private List<DevGetBoxDetailResponseDTO> details;

}
