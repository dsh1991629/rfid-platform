package com.rfid.platform.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON工具类
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 注册Java 8时间模块
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 对象转JSON字符串
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON失败", e);
            return null;
        }
    }

    /**
     * JSON字符串转对象
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * 对象转换
     * @param obj 源对象
     * @param clazz 目标类型
     * @return 目标对象
     */
    public static <T> T convertValue(Object obj, Class<T> clazz) {
        try {
            return objectMapper.convertValue(obj, clazz);
        } catch (Exception e) {
            log.error("对象转换失败", e);
            return null;
        }
    }
}