package org.ishare.oss;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({OssProperties.class})
@ConditionalOnProperty(prefix = "aliyun.oss", name = "buckets")
public class OssAutoConfiguration {
    public OssAutoConfiguration() {
    }

    @Bean
    public OssService ossService(OssProperties properties) {
        return new OssService(properties);
    }


    @Bean
    public StartEventListener ApplicationListener() {
        return new StartEventListener();
    }


}