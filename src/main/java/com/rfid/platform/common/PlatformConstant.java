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

    public interface STORAGE_ORDER_TYPE {
        Integer IN_BOUND = 1; // 入库单
        Integer OUT_BOUND = 2; // 出库单
        Integer INVENTORY_BOUND = 3; // 盘点单
    }

    public interface STORAGE_ORDER_STATUS {
        Integer SEND = 1; // 下发
        Integer EXECUTING = 2; // 盘点中
        Integer COMPLETED = 3; // 盘点完成
        Integer FINISHED = 4; // 上传完成
        Integer CANCELED = 5; // 取消
    }



    public interface ROLE_ALIAS {
        String SUPERADMIN = "administrator";
    }
}
