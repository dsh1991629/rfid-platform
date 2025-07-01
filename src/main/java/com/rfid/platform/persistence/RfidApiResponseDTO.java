package com.rfid.platform.persistence;

import lombok.Data;
import java.io.Serializable;

/**
 * RFID接口通用响应DTO
 */
@Data
public class RfidApiResponseDTO<T> implements Serializable {

    /**
     * 状态码，100表示成功
     */
    private Integer code;

    /**
     * 描述信息
     */
    private String message;

    /**
     * 是否成功标识
     */
    private Boolean success;

    /**
     * 时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String time;

    /**
     * 返回记录数（查询接口使用）
     */
    private Integer count;

    /**
     * 详情数据
     */
    private T data;

    public static <T> RfidApiResponseDTO<T> success() {
        RfidApiResponseDTO<T> response = new RfidApiResponseDTO<>();
        response.setCode(100);
        response.setMessage("操作成功");
        response.setSuccess(true);
        response.setTime(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return response;
    }

    public static <T> RfidApiResponseDTO<T> success(T data) {
        RfidApiResponseDTO<T> response = success();
        response.setData(data);
        return response;
    }

    public static <T> RfidApiResponseDTO<T> success(T data, Integer count) {
        RfidApiResponseDTO<T> response = success(data);
        response.setCount(count);
        return response;
    }

    public static <T> RfidApiResponseDTO<T> error(String message) {
        RfidApiResponseDTO<T> response = new RfidApiResponseDTO<>();
        response.setCode(200);
        response.setMessage(message);
        response.setSuccess(false);
        response.setTime(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return response;
    }
}