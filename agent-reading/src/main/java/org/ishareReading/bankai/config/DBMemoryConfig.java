package org.ishareReading.bankai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DBMemoryConfig {
    private String url;
    private String username;
    private String password;

    public String getUser() {
        return username;
    }
}
