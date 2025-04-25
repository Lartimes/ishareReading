package com.example.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "aliyun.oss", name = "buckets")
public class OssAutoConfiguration {

    @Bean
    public Map<String, OSS> ossClients(OssProperties properties) {
        Map<String, OSS> map = new HashMap<>();
        properties.getBuckets().forEach((name, config) -> {
            OSS ossClient = new OSSClientBuilder().build(
                config.getEndpoint(),
                config.getAccessKeyId(),
                config.getAccessKeySecret()
            );
            map.put(name, ossClient);
        });
        return map;
    }

    @Bean
    public OssService ossService(Map<String, OSS> ossClients, OssProperties properties) {
        return new OssService(ossClients, properties);
    }
}