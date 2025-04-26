package org.ishareReading.bankai;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.ishare.oss.OssProperties;
import org.ishareReading.bankai.model.BookOpinions;
import org.ishareReading.bankai.service.BookOpinionsService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;


@SpringBootApplication
@EnableConfigurationProperties(OssProperties.class)
@MapperScan(basePackages = {"org.ishareReading.bankai.mapper"})
@ComponentScan(basePackages = {"org.ishare.oss", "org.ishareReading.bankai"})
public class IReadingApplication implements CommandLineRunner {

    @Autowired
    private BookOpinionsService bookOpinionsService;

    public static void main(String[] args) {
        SpringApplication.run(IReadingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        BookOpinions entity = new BookOpinions();
        entity.setId(1916071924907986946L);
        entity.setOpinionText("这是一本好书22");
        bookOpinionsService.updateById(entity);
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
