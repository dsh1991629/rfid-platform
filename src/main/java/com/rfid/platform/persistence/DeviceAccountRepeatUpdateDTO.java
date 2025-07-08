package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "设备账户重复次数更新数据传输对象")
public class DeviceAccountRepeatUpdateDTO implements Serializable {

    @Schema(description = "账户ID", example = "1")
    private Long accountId;

    @Schema(description = "可重复登录次数，默认1", example = "2")
    private Integer repeatTimes;

}
