package com.rfid.platform.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.common.LanguageContext;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.RfidPlatformProperties;
import com.rfid.platform.exception.ExceptionEnum;
import com.rfid.platform.exception.ExceptionModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AuthorizeInterceptor implements HandlerInterceptor {


    @Autowired
    private RfidPlatformProperties rfidPlatformProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // header中获取语言
        String lang = request.getHeader(PlatformConstant.HTTP_CONFIG.HEADER_LANGUAGE_KEY);
        // 没有默认英语
        if (StringUtils.isBlank(lang)) {
            lang = PlatformConstant.HTTP_CONFIG.HEADER_LANGUAGE_DEFAULT_VALUE;
        }
        LanguageContext.setLanguage(lang);

        // 不需要鉴权的uri
        String uri = request.getRequestURI();
        List<String> ignoreSuffix = rfidPlatformProperties.getIgnoreUri();
        for (String suffix : ignoreSuffix) {
            if (uri.endsWith(suffix)) {
                return true;
            }
        }

        String token = request.getHeader(PlatformConstant.HTTP_CONFIG.HEADER_ACCESS_TOKEN);
        if (StringUtils.isBlank(token)) {
            handleResponse(request, response, ExceptionEnum.AUTHENTICATION_FAILED);
            return false;
        }

        String key = PlatformConstant.TOKEN_CONFIG.CACHE_KEY + token;
        if (!redisTemplate.hasKey(key)) {
            handleResponse(request, response, ExceptionEnum.AUTHENTICATION_FAILED);
            return false;
        }

        Object accountId = redisTemplate.opsForValue().get(key);
        AccountContext.setAccountId(Long.parseLong(String.valueOf(accountId)));

        // 刷新token时间
        redisTemplate.expire(key, PlatformConstant.TOKEN_CONFIG.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        LanguageContext.removeLanguage();
        AccountContext.removeAccountId();
    }


    private void handleResponse(HttpServletRequest req, HttpServletResponse res, ExceptionEnum exceptionEnum) {
        ExceptionModel model = new ExceptionModel();
        model.setCode(exceptionEnum.getCode());
        model.setMessage(exceptionEnum.getDes());
        model.setStatus(403);
        ResponseEntity<ExceptionModel> re = new ResponseEntity<>(model, HttpStatus.FORBIDDEN);
        handleHttpEntityResponse(re, new ServletWebRequest(req, res));
        try {
            res.flushBuffer();
        } catch (IOException e) {
            log.error("处理HTTP响应时发生错误", e);
        }
    }

    private <T> void handleHttpEntityResponse(ResponseEntity<T> responseEntity, ServletWebRequest webRequest) {
        try {
            HttpServletResponse response = webRequest.getResponse();
            if (response == null) {
                return;
            }
            
            // 设置HTTP状态码
            response.setStatus(responseEntity.getStatusCode().value());
            
            // 设置响应头
            responseEntity.getHeaders().forEach((headerName, headerValues) -> {
                headerValues.forEach(headerValue -> {
                    response.addHeader(headerName, headerValue);
                });
            });
            
            // 设置响应体
            if (responseEntity.getBody() != null) {
                response.setContentType("application/json;charset=UTF-8");
                String responseBody = new ObjectMapper().writeValueAsString(responseEntity.getBody());
                response.getWriter().write(responseBody);
            }
        } catch (Exception e) {
            log.error("处理HTTP响应时发生错误", e);
        }
    }
}
