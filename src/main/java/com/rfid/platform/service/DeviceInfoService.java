package com.rfid.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rfid.platform.entity.DeviceInfoBean;

public interface DeviceInfoService {

    boolean existDevice(LambdaQueryWrapper<DeviceInfoBean> nameCheckWrapper);

    boolean saveDevice(DeviceInfoBean deviceInfoBean);

    boolean deleteDevice(Long id);

    boolean updateDevice(DeviceInfoBean deviceInfoBean);

    IPage<DeviceInfoBean> pageDevice(Page<DeviceInfoBean> page, LambdaQueryWrapper<DeviceInfoBean> queryWrapper);

    DeviceInfoBean queryDeviceInfoByPk(Long id);

    DeviceInfoBean queryDeviceInfoByCode(String deviceCode);
}
