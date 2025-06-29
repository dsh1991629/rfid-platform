package com.rfid.platform.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 请求工具类
 */
public class RequestUtil {

    /**
     * 获取当前请求的HttpServletRequest对象
     * @return HttpServletRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }

    /**
     * 获取当前请求的HttpServletResponse对象
     * @return HttpServletResponse
     */
    public static HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getResponse();
        }
        return null;
    }

    /**
     * 获取请求头中的token
     * @return token字符串
     */
    public static String getTokenFromHeader() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

    /**
     * 获取客户端IP地址
     * @return IP地址
     */
    public static String getClientIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}