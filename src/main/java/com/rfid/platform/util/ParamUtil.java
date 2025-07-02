package com.rfid.platform.util;

import com.rfid.platform.persistence.RfidApiRequestDTO;
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
    public boolean validateBaseParams(RfidApiRequestDTO request, String expectedMethod) {
        if (request == null) {
            log.error("请求参数为空");
            return false;
        }

        if (!EXPECTED_APP_ID.equals(request.getAppId())) {
            log.error("AppId验证失败，期望：{}，实际：{}", EXPECTED_APP_ID, request.getAppId());
            return false;
        }

        if (!expectedMethod.equals(request.getMethod())) {
            log.error("Method验证失败，期望：{}，实际：{}", expectedMethod, request.getMethod());
            return false;
        }

        if (!EXPECTED_VERSION.equals(request.getVersion())) {
            log.error("Version验证失败，期望：{}，实际：{}", EXPECTED_VERSION, request.getVersion());
            return false;
        }

        if (!StringUtils.isNotBlank(request.getTimestamp())) {
            log.error("Timestamp不能为空");
            return false;
        }

        if (!StringUtils.isNotBlank(request.getSign())) {
            log.error("Sign不能为空");
            return false;
        }

        // 验证签名
        if (!RfidSignUtil.verifySign(request.getAppId(), request.getMethod(),
                request.getVersion(), request.getTimestamp(), request.getSign())) {
            log.error("签名验证失败");
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
