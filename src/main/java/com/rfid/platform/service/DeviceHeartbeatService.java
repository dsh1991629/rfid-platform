package com.rfid.platform.service;

public interface DeviceHeartbeatService {

    Long queryLoginNums(String deviceCode, Long deviceTimeout);

    boolean addLoginHeartBeat(String deviceCode, String accessToken, String timeStamp);

    boolean addLogoutHeartBeat(String accessToken, String timeStamp);

    boolean addDeviceHeartbeat(String accessToken, String timeStamp);
}
