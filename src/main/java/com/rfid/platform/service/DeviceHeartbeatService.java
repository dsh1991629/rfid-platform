package com.rfid.platform.service;

public interface DeviceHeartbeatService {

    Long queryLoginNums(String deviceCode, Long deviceTimeout);

    boolean updateLogout(String accessToken);

    boolean changeTimeoutLoginState(String deviceCode, Long deviceTimeout);

    boolean updateDeviceHeartbeat(String accessToken);
}
