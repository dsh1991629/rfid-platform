package com.rfid.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rfid.platform.rest")
@Data
public class PlatformRestProperties {

    private String version;
    
    private String url;
    
    private Integer connectTimeout = 5000;
    
    private Integer readTimeout = 10000;

}
