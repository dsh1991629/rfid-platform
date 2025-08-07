package com.rfid.platform.persistence;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Schema(description = "标签删除数据传输对象")
@Data
public class TagInfoDeleteDTO implements Serializable {

    /**
     * 设备ID
     */
    @Schema(description = "标签ID", example = "1", required = true)
    private Long id;


}
