package com.rfid.platform.persistence;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * RFID接口通用请求DTO
 */
@Data
@Schema(description = "RFID接口通用请求数据传输对象")
public class RfidApiRequestDTO<T> implements Serializable {

    /**
     * 时间戳，格式：yyyy-MM-dd HH:mm:ss
     */
    @Schema(description = "请求时间戳", example = "2024-01-01 12:00:00", required = true)
    private String timeStamp;

    /**
     * 版本号，固定值：3.0
     */
    @Schema(description = "API版本号", example = "1.0", required = true)
    private String version;


    /**
     * 业务参数
     */
    @Schema(description = "具体的业务参数，根据不同接口传入不同的数据结构")
    private T data;
}