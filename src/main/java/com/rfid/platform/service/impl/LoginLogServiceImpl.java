package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.config.handler.SuffixTableNameHandler;
import com.rfid.platform.entity.LoginLogBean;
import com.rfid.platform.mapper.LoginLogMapper;
import com.rfid.platform.service.LoginLogService;
import com.rfid.platform.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLogBean> implements LoginLogService {

    @Override
    @Async("loginLogExecutor")
    public void recordLoginLogAsync(Long accountId, String accountCode, String accountIp, String loginStatus, String errorMsg, String accessToken) {
        try {
            LoginLogBean loginLog = new LoginLogBean();
            loginLog.setAccountId(accountId);
            loginLog.setAccountCode(accountCode);
            loginLog.setAccountIp(accountIp);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setLoginStatus(loginStatus);
            loginLog.setErrorMsg(errorMsg);
            loginLog.setAccessToken(accessToken);

            String month = TimeUtil.getMonthNoLineString(TimeUtil.getSysDate());
            SuffixTableNameHandler.setData(month);
            this.save(loginLog);
            log.info("登录日志记录成功: accountCode={}, status={}", accountCode, loginStatus);
        } catch (Exception e) {
            log.error("记录登录日志失败: accountCode={}, error={}", accountCode, e.getMessage(), e);
        } finally {
            SuffixTableNameHandler.removeData();
        }
    }

    @Override
    @Async("loginLogExecutor")
    public void updateLogoutTimeAsync(String accessToken) {
        try {
            LambdaUpdateWrapper<LoginLogBean> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(LoginLogBean::getAccessToken, accessToken)
                        .eq(LoginLogBean::getLoginStatus, PlatformConstant.LOGIN_STATUS.SUCCESS)
                        .isNull(LoginLogBean::getLogoutTime)
                        .set(LoginLogBean::getLogoutTime, LocalDateTime.now());

            String month = TimeUtil.getMonthNoLineString(TimeUtil.getSysDate());
            SuffixTableNameHandler.setData(month);

            this.update(updateWrapper);
            log.info("登出时间更新成功: accessToken={}", accessToken);
        } catch (Exception e) {
            log.error("更新登出时间失败: accessToken={}, error={}", accessToken, e.getMessage(), e);
        } finally {
            SuffixTableNameHandler.removeData();
        }
    }
}
