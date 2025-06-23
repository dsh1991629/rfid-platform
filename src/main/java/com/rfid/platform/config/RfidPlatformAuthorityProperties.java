package com.rfid.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "rfid.platform")
@Data
public class RfidPlatformAuthorityProperties {

    private List<String> ignoreUri = new ArrayList<>();

}
