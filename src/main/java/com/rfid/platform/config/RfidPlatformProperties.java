package com.rfid.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "rfid.platform")
@Data
public class RfidPlatformProperties {

    private List<String> ignoreUri = new ArrayList<>();

    private Integer captchaBit = 1;

    private Long deviceTimeout = 7200L;

    private Long wmsTimeout = 7200L;

}
