package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "发货方信息")
public class Rms2ErpOrderSenderInfoRespDTO implements Serializable {

    @Schema(description = "名字")
    private String name;

    @Schema(description = "地址信息")
    private String address;

    @Schema(description = "联系方式")
    private String contact;
}
