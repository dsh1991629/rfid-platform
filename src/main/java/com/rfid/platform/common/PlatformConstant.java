package com.rfid.platform.common;

public class PlatformConstant {

    private PlatformConstant (){}

    public interface HTTP_CONFIG {
        String HEADER_LANGUAGE_KEY = "lang";
        String HEADER_LANGUAGE_DEFAULT_VALUE = "zh";
        String HEADER_ACCESS_TOKEN = "access-token";
    }

    public interface TOKEN_CONFIG {
        String CACHE_KEY = "rfid:token:";
        Integer TIMEOUT_SECONDS = 60 * 60 * 24;
    }

    public interface RET_CODE {
        String SUCCESS = "00";
        String FAILED = "99";
    }

    public interface CACHE_KEY {
        String CAPTCHA_KEY = "rfid:captcha:";

    }

}
