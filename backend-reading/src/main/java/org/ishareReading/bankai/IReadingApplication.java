package org.ishareReading.bankai;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.ishare.oss.OssProperties;
import org.ishareReading.bankai.holder.UserHolder;
import org.ishareReading.bankai.model.BookOpinions;
import org.ishareReading.bankai.model.Books;
import org.ishareReading.bankai.schedul.HotRank;
import org.ishareReading.bankai.service.BookOpinionsService;
import org.ishareReading.bankai.service.BooksService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;


@SpringBootApplication
@EnableConfigurationProperties(OssProperties.class)
@MapperScan(basePackages = {"org.ishareReading.bankai.mapper"})
@ComponentScan(basePackages = {"org.ishare.oss", "org.ishareReading.bankai"})
@EnableScheduling
public class IReadingApplication implements CommandLineRunner {

    @Autowired
    private BookOpinionsService bookOpinionsService;

    @Autowired
    private BooksService booksService;

    @Autowired
    private HotRank hotRank;

    public static void main(String[] args) {
        SpringApplication.run(IReadingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Books books = new Books();
        books.setName("Red Book");
        books.setUploadTime(LocalDateTime.now());
        books.setGenre("盗墓");
        books.setId(1916713914342154241L);
        booksService.updateById(books);
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
