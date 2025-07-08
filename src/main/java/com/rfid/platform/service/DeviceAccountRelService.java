package com.rfid.platform.service;

import com.rfid.platform.entity.DeviceAccountRelBean;

import com.rfid.platform.persistence.DeviceAccountRepeatUpdateDTO;
import java.util.List;

public interface DeviceAccountRelService {

    List<DeviceAccountRelBean> listDeviceAccountRel(Long deviceId);

    boolean deleteDeviceAccountRel(Long deviceId);

    boolean deleteDeviceAccountRelWithAccount(Long deviceId, List<Long> accountIds);

    boolean updateDeviceAccountRel(Long deviceId, List<DeviceAccountRepeatUpdateDTO> accounts);
}
