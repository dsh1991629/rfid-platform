package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.entity.DeviceHeartbeatBean;
import com.rfid.platform.persistence.storage.HeartBeatDTO;

public interface DeviceHeartbeatService {

    Long queryLoginNums(String deviceCode, Long deviceTimeout);

    boolean addLoginHeartBeat(String deviceCode, String accessToken, String timeStamp);

    boolean addLogoutHeartBeat(String accessToken, String timeStamp);

    boolean addDeviceHeartbeat(String accessToken, String timeStamp, HeartBeatDTO heartBeatDTO);

    IPage<DeviceHeartbeatBean> pageDeviceHeartbeat(Page<DeviceHeartbeatBean> page, LambdaQueryWrapper<DeviceHeartbeatBean> queryWrapper);
}
