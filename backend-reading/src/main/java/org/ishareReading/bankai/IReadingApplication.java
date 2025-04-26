package org.ishareReading.bankai;

import org.ishare.oss.OssProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableConfigurationProperties(OssProperties.class)
@MapperScan(basePackages = {"org.ishareReading.bankai.mapper"})
@ComponentScan(basePackages = {"org.ishare.oss" , "org.ishareReading.bankai"})
public class IReadingApplication {

    public static void main(String[] args) {
        SpringApplication.run(IReadingApplication.class, args);
    }
}
