package com.rfid.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rfid.platform.entity.DeviceInfoBean;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceInfoMapper extends BaseMapper<DeviceInfoBean> {
}
