package org.ishareReading.bankai.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    private String url;


    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    @Primary
    @Bean
    public DataSource longConnectionDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
//        String replace = url.replace("p6spy:", "");
//        jdbc:postgresql://localhost:5432/isharereading
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(5); // 根据需要调整池大小
        dataSource.setMinimumIdle(2);    // 最小空闲连接数
        dataSource.setIdleTimeout(300000); // 5 分钟（300,000 毫秒）
        dataSource.setMaxLifetime(300000); // 5 分钟（300,000 毫秒）
        return dataSource;
    }
}