package com.rfid.platform.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rfid.platform.persistence.RfidApiRequestDTO;
import com.rfid.platform.service.TagRestService;
import com.rfid.platform.util.JsonUtil;
import com.rfid.platform.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TagRestServiceImpl implements TagRestService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public JSONObject executeRestPostOptions(String version, String url, JSONObject reqObject) {
        RfidApiRequestDTO<JSONObject> request = new RfidApiRequestDTO<>();
        request.setVersion(version);
        request.setTimeStamp(TimeUtil.getDateFormatterString(TimeUtil.getSysDate()));
        request.setData(reqObject);

        // 使用RestTemplate调用接口，返回String
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RfidApiRequestDTO<JSONObject>> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                httpEntity,
                String.class
        );

        String responseBody = response.getBody();

        // 将String转换成JSONObject并返回
        if (responseBody != null && !responseBody.isEmpty()) {
            return JSONObject.parseObject(responseBody);
        }

        return new JSONObject();
    }

    
    @Override
    public <T, R> R executeRestPostOptions(String url, RfidApiRequestDTO<T> requestDTO, TypeReference<R> responseType) {
        // 使用RestTemplate调用接口，返回String
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RfidApiRequestDTO<T>> httpEntity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                httpEntity,
                String.class
        );

        String responseBody = response.getBody();

        // 将String转换成指定类型并返回
        if (StringUtils.isNotBlank(responseBody)) {
            return JsonUtil.fromJson(responseBody, responseType);
        }

        return null;
    }
}
