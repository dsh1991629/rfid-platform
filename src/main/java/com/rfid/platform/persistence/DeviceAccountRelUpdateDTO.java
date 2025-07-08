package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "设备账号更新数据传输对象")
public class DeviceAccountRelUpdateDTO implements Serializable {

    @Schema(description = "设备ID", example = "1", required = true)
    private Long id;

    @Schema(description = "账户与登录次数", required = true)
    private List<DeviceAccountRepeatUpdateDTO> deviceAccounts;
}
