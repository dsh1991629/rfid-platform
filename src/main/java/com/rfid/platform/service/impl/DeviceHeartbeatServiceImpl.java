package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.DeviceHeartbeatBean;
import com.rfid.platform.mapper.DeviceHeartbeatMapper;
import com.rfid.platform.service.DeviceHeartbeatService;
import com.rfid.platform.util.TimeUtil;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class DeviceHeartbeatServiceImpl extends ServiceImpl<DeviceHeartbeatMapper, DeviceHeartbeatBean> implements DeviceHeartbeatService {


    @Override
    public Long queryLoginNums(String deviceCode, Long deviceTimeout) {
        LocalDateTime now = TimeUtil.getSysDate();
        // 开始时间等于当前时间now减去超时的秒数deviceTimeout
        LocalDateTime startTime = now.minusSeconds(deviceTimeout);
        LambdaQueryWrapper<DeviceHeartbeatBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DeviceHeartbeatBean::getDeviceCode, deviceCode);
        queryWrapper.isNotNull(DeviceHeartbeatBean::getLoginTime);
        queryWrapper.ge(DeviceHeartbeatBean::getLoginTime, startTime);
        queryWrapper.isNull(DeviceHeartbeatBean::getLogoutTime);
        return super.count(queryWrapper);
    }


    @Override
    public boolean updateLogout(String accessToken) {
        LambdaUpdateWrapper<DeviceHeartbeatBean> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(DeviceHeartbeatBean::getAccessToken, accessToken);
        updateWrapper.set(DeviceHeartbeatBean::getLogoutTime, TimeUtil.getSysDate());
        return super.update(updateWrapper);
    }


    @Override
    public boolean changeTimeoutLoginState(String deviceCode, Long deviceTimeout) {
        LocalDateTime now = TimeUtil.getSysDate();
        // 开始时间等于当前时间now减去超时的秒数deviceTimeout
        LocalDateTime startTime = now.minusSeconds(deviceTimeout);
        LambdaUpdateWrapper<DeviceHeartbeatBean> queryWrapper = Wrappers.lambdaUpdate();
        queryWrapper.eq(DeviceHeartbeatBean::getDeviceCode, deviceCode);
        queryWrapper.isNotNull(DeviceHeartbeatBean::getLoginTime);
        queryWrapper.lt(DeviceHeartbeatBean::getLoginTime, startTime);
        queryWrapper.isNull(DeviceHeartbeatBean::getLogoutTime);
        queryWrapper.set(DeviceHeartbeatBean::getLogoutTime, now);
        return super.update(queryWrapper);
    }
}
