package com.rfid.platform.enums;

import com.rfid.platform.common.PlatformConstant;

/**
 * 设备心跳类型枚举
 */
public enum DeviceHeartbeatTypeEnum {
    
    LOGIN(PlatformConstant.DEVICE_HEARTBEAT_TYPE.LOGIN, "登录"),
    HEARTBEAT(PlatformConstant.DEVICE_HEARTBEAT_TYPE.HEARTBEAT, "心跳"),
    LOGOUT(PlatformConstant.DEVICE_HEARTBEAT_TYPE.LOGOUT, "登出");
    
    private final Integer code;
    private final String description;
    
    DeviceHeartbeatTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取描述
     * @param code 类型代码
     * @return 类型描述，如果找不到则返回"未知类型"
     */
    public static String getDescriptionByCode(Integer code) {
        if (code == null) {
            return "未知类型";
        }
        
        for (DeviceHeartbeatTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDescription();
            }
        }
        return "未知类型";
    }
    
    /**
     * 根据代码获取枚举实例
     * @param code 类型代码
     * @return 枚举实例，如果找不到则返回null
     */
    public static DeviceHeartbeatTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (DeviceHeartbeatTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}