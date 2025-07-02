package com.rfid.platform.util;

import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.persistence.RfidApiResponseDTO;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ParamUtil {

    private static final String EXPECTED_APP_ID = "hd-rfid-dev";
    private static final String EXPECTED_VERSION = "3.0";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 验证基础参数
     */
    public boolean validateBaseParams(RfidApiRequestDTO request, String expectedMethod, RfidApiResponseDTO response) {
        if (request == null) {
            log.error("请求参数为空");
            response.setCode("300");
            response.setMessage("请求参数为空");
            return false;
        }

        if (!EXPECTED_APP_ID.equals(request.getAppId())) {
            log.error("AppId验证失败，期望：{}，实际：{}", EXPECTED_APP_ID, request.getAppId());
            response.setCode("201");
            response.setMessage("AppId验证失败，期望："+EXPECTED_APP_ID+"，实际：" + request.getAppId());
            return false;
        }

        if (!expectedMethod.equals(request.getMethod())) {
            log.error("Method验证失败，期望：{}，实际：{}", expectedMethod, request.getMethod());
            response.setCode("203");
            response.setMessage("Method验证失败，期望："+expectedMethod+"，实际：" + request.getMethod());
            return false;
        }

        if (!EXPECTED_VERSION.equals(request.getVersion())) {
            log.error("Version验证失败，期望：{}，实际：{}", EXPECTED_VERSION, request.getVersion());
            response.setCode("202");
            response.setMessage("Version验证失败，期望："+EXPECTED_VERSION+"，实际："+ request.getVersion());
            return false;
        }

        if (!StringUtils.isNotBlank(request.getTimestamp())) {
            log.error("Timestamp不能为空");
            response.setCode("204");
            response.setMessage("Timestamp不能为空");
            return false;
        }

        if (!StringUtils.isNotBlank(request.getSign())) {
            log.error("Sign不能为空");
            response.setCode("210");
            response.setMessage("Sign不能为空");
            return false;
        }

        // 验证签名
        if (!RfidSignUtil.verifySign(request.getAppId(), request.getMethod(),
                request.getVersion(), request.getTimestamp(), request.getSign())) {
            log.error("签名验证失败");
            response.setCode("200");
            response.setMessage("签名验证失败");
            return false;
        }

        return true;
    }


    /**
     * 解析业务参数
     */
    public  <T> T parseParam(Object param, Class<T> clazz) {
        try {
            if (param == null) {
                return null;
            }
            // 使用JsonUtil进行对象转换
            return JsonUtil.convertValue(param, clazz);
        } catch (Exception e) {
            log.error("参数解析失败", e);
            return null;
        }
    }

}
