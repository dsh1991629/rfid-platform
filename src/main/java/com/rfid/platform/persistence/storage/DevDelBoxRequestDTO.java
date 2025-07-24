package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Schema(description = "删除箱请求数据传输对象")
@Data
public class DevDelBoxRequestDTO implements Serializable {

    @Schema(description = "箱外码")
    private List<String> boxCodes;

}
