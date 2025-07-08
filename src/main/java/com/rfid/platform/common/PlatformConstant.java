package com.rfid.platform.common;

public class PlatformConstant {

    public interface CACHE_KEY {
        String CAPTCHA_KEY = "captcha:";
        String TOKEN_KEY = "token:";
        String RESET_PASSWORD = "reset_password:";
        String LOGIN_FAIL_COUNT = "login_fail_count:";
        String ACCOUNT_LOCK = "account_lock:";
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

    public interface LOGIN_CONFIG {
        int MAX_LOGIN_FAIL_COUNT = 5; // 最大登录失败次数
        int LOCK_DURATION_MINUTES = 30; // 锁定时长（分钟）
    }

    public interface LOGIN_STATUS {
        String SUCCESS = "SUCCESS";
        String FAILED = "FAILED";
        String LOCKED = "LOCKED";
    }

    public interface STORAGE_OPERATION_TYPE {
        Integer STORAGE_IN = 1;
        Integer STORAGE_OUT = 2;
    }

    public interface STORAGE_TASK_STATE {
        Integer CREATED = 1;
        Integer RUNNING = 2;
        Integer PARTIAL = 3;
        Integer SUCCESS = 4;
    }

    public interface ROLE_ALIAS {
        String SUPERADMIN = "administrator";
    }
}
