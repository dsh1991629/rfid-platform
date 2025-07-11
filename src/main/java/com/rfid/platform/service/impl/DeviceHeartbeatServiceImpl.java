package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.DeviceHeartbeatBean;
import com.rfid.platform.mapper.DeviceHeartbeatMapper;
import com.rfid.platform.service.DeviceHeartbeatService;
import com.rfid.platform.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
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
        return super.count(queryWrapper);
    }


    @Override
    public boolean addLoginHeartBeat(String deviceCode, String accessToken, String timeStamp) {
        DeviceHeartbeatBean deviceHeartbeatBean = new DeviceHeartbeatBean();
        deviceHeartbeatBean.setDeviceCode(deviceCode);
        deviceHeartbeatBean.setAccessToken(accessToken);
        deviceHeartbeatBean.setCreateDate(timeStamp);
        deviceHeartbeatBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        deviceHeartbeatBean.setType(PlatformConstant.DEVICE_HEARTBEAT_TYPE.LOGIN);
        return super.save(deviceHeartbeatBean);
    }


    @Override
    public boolean addLogoutHeartBeat(String accessToken, String timeStamp) {
        String deviceCode = "";
        LambdaQueryWrapper<DeviceHeartbeatBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DeviceHeartbeatBean::getAccessToken, accessToken);
        List<DeviceHeartbeatBean> deviceHeartbeatBeans = super.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(deviceHeartbeatBeans)) {
            deviceCode = deviceHeartbeatBeans.get(0).getDeviceCode();
        }
        DeviceHeartbeatBean deviceHeartbeatBean = new DeviceHeartbeatBean();
        deviceHeartbeatBean.setDeviceCode(deviceCode);
        deviceHeartbeatBean.setAccessToken(accessToken);
        deviceHeartbeatBean.setCreateDate(timeStamp);
        deviceHeartbeatBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        deviceHeartbeatBean.setType(PlatformConstant.DEVICE_HEARTBEAT_TYPE.LOGOUT);
        return super.save(deviceHeartbeatBean);
    }


    @Override
    public boolean addDeviceHeartbeat(String accessToken, String timeStamp) {
        String deviceCode = "";
        LambdaQueryWrapper<DeviceHeartbeatBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DeviceHeartbeatBean::getAccessToken, accessToken);
        List<DeviceHeartbeatBean> deviceHeartbeatBeans = super.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(deviceHeartbeatBeans)) {
            deviceCode = deviceHeartbeatBeans.get(0).getDeviceCode();
        }
        DeviceHeartbeatBean deviceHeartbeatBean = new DeviceHeartbeatBean();
        deviceHeartbeatBean.setDeviceCode(deviceCode);
        deviceHeartbeatBean.setAccessToken(accessToken);
        deviceHeartbeatBean.setCreateDate(timeStamp);
        deviceHeartbeatBean.setCreateTime(TimeUtil.localDateTimeToTimestamp(TimeUtil.parseDateFormatterString(timeStamp)));
        deviceHeartbeatBean.setType(PlatformConstant.DEVICE_HEARTBEAT_TYPE.HEARTBEAT);
        return super.save(deviceHeartbeatBean);
    }
}
