package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.DeviceInfoBean;
import com.rfid.platform.mapper.DeviceInfoMapper;
import com.rfid.platform.service.DeviceAccountRelService;
import com.rfid.platform.service.DeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfoBean> implements DeviceInfoService {

    @Autowired
    @Lazy
    private DeviceAccountRelService deviceAccountRelService;


    @Override
    public boolean existDevice(LambdaQueryWrapper<DeviceInfoBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }

    @Override
    public boolean saveDevice(DeviceInfoBean deviceInfoBean) {
        return super.save(deviceInfoBean);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteDevice(Long id) {
        deviceAccountRelService.deleteDeviceAccountRel(id);
        return super.removeById(id);
    }

    @Override
    public boolean updateDevice(DeviceInfoBean deviceInfoBean) {
        return super.updateById(deviceInfoBean);
    }

    @Override
    public IPage<DeviceInfoBean> pageDevice(Page<DeviceInfoBean> page, LambdaQueryWrapper<DeviceInfoBean> queryWrapper) {
        return super.page(page, queryWrapper);
    }

    @Override
    public DeviceInfoBean queryDeviceInfoByPk(Long id) {
        return super.getById(id);
    }
}
