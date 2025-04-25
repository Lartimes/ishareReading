package org.ishareReading.bankai.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.ishareReading.bankai.utils.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public IdentifierGenerator idGenerator() {
        return new IdentifierGenerator() {
            @Override
            public Long nextId(Object entity) {
                return IdUtil.getId();
            }
        };
    }
} 