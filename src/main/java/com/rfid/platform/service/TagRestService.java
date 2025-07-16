package com.rfid.platform.service;

import com.alibaba.fastjson2.JSONObject;

public interface TagRestService {

    JSONObject executeRestPostOptions(String version, String url, JSONObject reqObject);
}
