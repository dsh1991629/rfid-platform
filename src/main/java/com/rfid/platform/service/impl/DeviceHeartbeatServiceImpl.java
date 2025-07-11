package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.common.PlatformConstant;
import com.rfid.platform.entity.DeviceHeartbeatBean;
import com.rfid.platform.mapper.DeviceHeartbeatMapper;
import com.rfid.platform.service.DeviceHeartbeatService;
import com.rfid.platform.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
public class DeviceHeartbeatServiceImpl extends ServiceImpl<DeviceHeartbeatMapper, DeviceHeartbeatBean> implements DeviceHeartbeatService {


    @Override
    public Long queryLoginNums(String deviceCode, Long deviceTimeout) {
        LocalDateTime now = TimeUtil.getSysDate();
        // 开始时间等于当前时间now减去超时的秒数deviceTimeout
        LocalDateTime startDate = now.minusSeconds(deviceTimeout);
        Long startTime = TimeUtil.localDateTimeToTimestamp(startDate);
        
        // 查询设备编码等于deviceCode，createTime大于等于startTime，按access_token分组，分组中不能有type等于3，统计分组的数量
        // 首先查询符合条件的所有记录
        LambdaQueryWrapper<DeviceHeartbeatBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DeviceHeartbeatBean::getDeviceCode, deviceCode)
                    .ge(DeviceHeartbeatBean::getCreateTime, startTime);
        
        List<DeviceHeartbeatBean> allRecords = super.list(queryWrapper);
        
        // 按access_token分组，统计每组中没有type=3(LOGOUT)的分组数量
        Map<String, List<DeviceHeartbeatBean>> groupedByToken = allRecords.stream()
                .collect(Collectors.groupingBy(DeviceHeartbeatBean::getAccessToken));
        
        // 统计分组中不包含type=3的分组数量
        long activeLoginCount = groupedByToken.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .noneMatch(record -> PlatformConstant.DEVICE_HEARTBEAT_TYPE.LOGOUT.equals(record.getType())))
                .count();
        
        return activeLoginCount;
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


    @Override
    public IPage<DeviceHeartbeatBean> pageDeviceHeartbeat(Page<DeviceHeartbeatBean> page, LambdaQueryWrapper<DeviceHeartbeatBean> queryWrapper) {
        return super.page(page, queryWrapper);
    }
}
