package com.crazy.config;

import com.crazy.properties.QcosProperties;
import com.crazy.utils.QcosUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CosConfiguration {

    @Bean
    @ConditionalOnMissingBean // 只有容器中不存在指定bean时才创建该bean
    public QcosUtil createQCOSUtil(@Autowired QcosProperties qcos) {
        log.info("Start to create QCloud COS file upload util object...");
        return new QcosUtil(qcos.getRegion(), qcos.getAccessSecretId(),
                qcos.getAccessSecretKey(), qcos.getBucketName());
    }
}
