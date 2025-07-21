package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "货主信息")
public class Rms2ErpOrderOwnerInfoRespDTO implements Serializable {

    @Schema(description = "名字", example = "某某公司")
    private String name;

    @Schema(description = "联系方式", example = "13800138000")
    private String contact;
}
