package com.rfid.platform.service;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rfid.platform.persistence.RfidApiRequestDTO;

public interface TagRestService {

    JSONObject executeRestPostOptions(String version, String url, JSONObject reqObject);

    <T, R> R executeRestPostOptions(String url, RfidApiRequestDTO<T> requestDTO, TypeReference<R> responseType);

}
