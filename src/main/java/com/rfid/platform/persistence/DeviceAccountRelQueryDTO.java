package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "设备账号查询数据传输对象")
public class DeviceAccountRelQueryDTO implements Serializable {

    @Schema(description = "设备ID", example = "1")
    private Long id;

}
