package org.ishareReading.bankai;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.ishare.oss.OssProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;


@EnableElasticsearchRepositories(basePackages = "org.ishareReading.bankai.es")
@EnableConfigurationProperties({OssProperties.class})
@ComponentScan(basePackages = {"org.ishare.oss", "org.ishareReading.bankai"})
@MapperScan(basePackages = {"org.ishareReading.bankai.mapper"})
@SpringBootApplication(scanBasePackages = {"org.ishareReading", "org.ishare"})
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class IReadingApplication {


    public static void main(String[] args) {
        SpringApplication.run(IReadingApplication.class, args);
    }


    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateAt", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateAt", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
