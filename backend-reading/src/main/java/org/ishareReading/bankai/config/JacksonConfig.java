package org.ishareReading.bankai.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

// 配置 Jackson 序列化器,前端bigint溢出
@Configuration
public class JacksonConfig {
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, new LongToStringSerializer());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().registerModule(module);
        return converter;
    }
}    