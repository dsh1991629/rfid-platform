package com.rfid.platform.persistence;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * RFID接口通用请求DTO
 */
@Data
public class RfidApiRequestDTO implements Serializable {

    /**
     * 应用ID，固定值：hd-rfid-dev
     */
    private String appId;

    /**
     * 方法名
     */
    private String method;

    /**
     * 时间戳，格式：yyyy-MM-dd HH:mm:ss
     */
    private String timestamp;

    /**
     * 版本号，固定值：3.0
     */
    private String version;

    /**
     * 签名
     */
    private String sign;

    /**
     * 业务参数
     */
    private Object param;
}