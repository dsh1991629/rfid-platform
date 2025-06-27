package com.rfid.platform.common;

public class PlatformConstant {

    public interface CACHE_KEY {
        String CAPTCHA_KEY = "captcha:";
        String TOKEN_KEY = "token:";
    }


    public interface HTTP_CONFIG {
        String HEADER_LANGUAGE_KEY = "lang";
        String HEADER_LANGUAGE_DEFAULT_VALUE = "zh";
        String HEADER_ACCESS_TOKEN = "access-token";
    }

    public interface TOKEN_CONFIG {
        String TOKEN_CACHE_KEY = "rfid:token:";
        Integer TIMEOUT_SECONDS = 60 * 60 * 24;
    }

    public interface RET_CODE {
        String SUCCESS = "00";
        String FAILED = "99";
    }
}
