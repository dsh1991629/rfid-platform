package com.rfid.platform.persistence;

import java.time.format.DateTimeFormatter;
import lombok.Data;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * RFID接口通用响应DTO
 */
@Data
@Schema(description = "RFID接口通用响应数据传输对象")
public class RfidApiResponseDTO<T> implements Serializable {

    /**
     * 是否成功标识
     */
    @Schema(description = "操作是否成功", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean status;

    /**
     * 描述信息
     */
    @Schema(description = "响应描述信息", example = "操作成功", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    /**
     * 时间，格式：yyyy-MM-dd HH:mm:ss
     */
    @Schema(description = "响应时间", example = "2024-01-01 12:00:00", pattern = "yyyy-MM-dd HH:mm:ss", requiredMode = Schema.RequiredMode.REQUIRED)
    private String timeStamp;

    /**
     * 详情数据
     */
    @Schema(description = "响应数据内容")
    private T data;

    /**
     * 创建成功响应
     * @return 成功响应对象
     */
    public static <T> RfidApiResponseDTO<T> success() {
        RfidApiResponseDTO<T> response = new RfidApiResponseDTO<>();
        response.setMessage("操作成功");
        response.setStatus(true);
        response.setTimeStamp(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return response;
    }

    /**
     * 创建带数据的成功响应
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> RfidApiResponseDTO<T> success(T data) {
        RfidApiResponseDTO<T> response = success();
        response.setData(data);
        return response;
    }


    /**
     * 创建错误响应
     * @param message 错误信息
     * @return 错误响应对象
     */
    public static <T> RfidApiResponseDTO<T> error(String message) {
        RfidApiResponseDTO<T> response = new RfidApiResponseDTO<>();
        response.setMessage(message);
        response.setStatus(false);
        response.setTimeStamp(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return response;
    }
}