package com.crazy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "crazy.qcos")
public class QcosProperties {

    private String region; // 存储桶所在的地域
    private String accessSecretId;
    private String accessSecretKey;
    private String bucketName;

}
