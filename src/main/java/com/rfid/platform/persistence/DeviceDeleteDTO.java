package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "设备删除数据传输对象")
@Data
public class DeviceDeleteDTO implements Serializable {

    /**
     * 设备ID
     */
    @Schema(description = "设备ID", example = "1", required = true)
    private Long id;


}
