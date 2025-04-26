package org.ishare.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "aliyun.oss", name = "buckets")
@ConditionalOnMissingBean(OSS.class)
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

    @Bean
    public StartEventListener ApplicationListener() {
        return new StartEventListener();
    }


}