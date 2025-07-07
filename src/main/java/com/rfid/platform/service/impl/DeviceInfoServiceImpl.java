package com.rfid.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rfid.platform.entity.DeviceInfoBean;
import com.rfid.platform.mapper.DeviceInfoMapper;
import com.rfid.platform.service.DeviceInfoService;
import org.springframework.stereotype.Service;

@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfoBean> implements DeviceInfoService {

    @Override
    public boolean existDevice(LambdaQueryWrapper<DeviceInfoBean> nameCheckWrapper) {
        return super.exists(nameCheckWrapper);
    }

    @Override
    public boolean saveDevice(DeviceInfoBean deviceInfoBean) {
        return super.save(deviceInfoBean);
    }

    @Override
    public boolean deleteDevice(Long id) {
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
}
