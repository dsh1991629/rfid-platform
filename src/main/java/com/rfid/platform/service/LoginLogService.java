package com.rfid.platform.service;

public interface LoginLogService {
    
    /**
     * 异步记录登录日志
     */
    void recordLoginLogAsync(Long accountId, String accountCode, String accountIp, String loginStatus, String errorMsg, String accessToken);
    
    /**
     * 异步更新登出时间
     */
    void updateLogoutTimeAsync(String accessToken);
}
