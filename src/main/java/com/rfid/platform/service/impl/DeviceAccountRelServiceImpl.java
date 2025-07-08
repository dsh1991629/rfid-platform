package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.entity.DeviceAccountRelBean;
import com.rfid.platform.mapper.DeviceAccountRelMapper;
import com.rfid.platform.persistence.DeviceAccountRepeatUpdateDTO;
import com.rfid.platform.service.DeviceAccountRelService;
import com.rfid.platform.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceAccountRelServiceImpl extends ServiceImpl<DeviceAccountRelMapper, DeviceAccountRelBean> implements DeviceAccountRelService {


    @Override
    public List<DeviceAccountRelBean> listDeviceAccountRel(Long deviceId) {
        LambdaQueryWrapper<DeviceAccountRelBean> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceAccountRelBean::getDeviceId, deviceId);
        return super.list(queryWrapper);
    }

    @Override
    public boolean deleteDeviceAccountRel(Long deviceId) {
        LambdaQueryWrapper<DeviceAccountRelBean> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DeviceAccountRelBean::getDeviceId, deviceId);
        return super.remove(queryWrapper);
    }

    @Override
    public boolean deleteDeviceAccountRelWithAccount(Long deviceId, List<Long> accountIds) {
        LambdaQueryWrapper<DeviceAccountRelBean> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DeviceAccountRelBean::getDeviceId, deviceId);
        queryWrapper.in(DeviceAccountRelBean::getAccountId, accountIds);
        return super.remove(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateDeviceAccountRel(Long deviceId, List<DeviceAccountRepeatUpdateDTO> accounts) {
        deleteDeviceAccountRel(deviceId);
        LocalDateTime now = TimeUtil.getSysDate();
        Long accountId = AccountContext.getAccountId();
        List<DeviceAccountRelBean> deviceAccountRelBeans = accounts.stream().map(e -> {
            DeviceAccountRelBean deviceAccountRelBean = new DeviceAccountRelBean();
            deviceAccountRelBean.setDeviceId(deviceId);
            deviceAccountRelBean.setAccountId(e.getAccountId());
            deviceAccountRelBean.setRepeatTimes(e.getRepeatTimes());
            deviceAccountRelBean.setCreateId(accountId);
            deviceAccountRelBean.setCreateTime(now);
            return deviceAccountRelBean;
        }).collect(Collectors.toUnmodifiableList());
        return super.saveBatch(deviceAccountRelBeans);
    }
}
