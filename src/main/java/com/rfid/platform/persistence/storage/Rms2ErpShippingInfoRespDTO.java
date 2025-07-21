package com.rfid.platform.persistence.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "物流信息响应数据传输对象")
@Data
public class Rms2ErpShippingInfoRespDTO implements Serializable {

    // 响应中的data字段为空对象，所以这里不需要添加具体字段
    // 如果后续需要返回具体数据，可以在这里添加相应字段
}