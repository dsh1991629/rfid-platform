package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "设备账号删除数据传输对象")
public class DeviceAccountRelDeleteDTO implements Serializable {

    @Schema(description = "设备ID", example = "1", required = true)
    private Long id;

    @Schema(description = "账户ID", example = "[1,2,3]", required = true)
    private List<Long> accountIds;
}
