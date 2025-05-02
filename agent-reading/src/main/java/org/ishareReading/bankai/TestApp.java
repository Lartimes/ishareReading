package org.ishareReading.bankai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"org.ishareReading.bankai.mapper"})
@SpringBootApplication(scanBasePackages = {"org.ishareReading"})
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
