package com.crazy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "crazy.wechat")
public class WeChatProperties {
    private String appid;
    private String secret;
}
