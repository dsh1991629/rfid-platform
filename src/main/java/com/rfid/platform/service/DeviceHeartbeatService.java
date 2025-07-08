package com.rfid.platform.service;

public interface DeviceHeartbeatService {

    Long queryLoginNums(String deviceCode);

    boolean updateLogout(String accessToken);
}
