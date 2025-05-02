package org.ishareReading.bankai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class DBMemoryConfig {
//      datasource:
//    driver-class-name: org.postgresql.Driver
//    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:54321}/${DB_NAME:isharereading}
//    username: ${DB_USERNAME:postgres}
//    password: ${DB_PASSWORD:postgres}

    @Value("${spring.datasource.username:}")
    private String user;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${spring.datasource.url:}")
    private String url;

}
