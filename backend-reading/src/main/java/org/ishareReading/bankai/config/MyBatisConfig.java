package org.ishareReading.bankai.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.SqlSessionFactory;
import org.ishareReading.bankai.utils.IdUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class MyBatisConfig {
    @Bean
    public SqlInjector sqlInjector() {
        return new SqlInjector();
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

    @Bean
    public GlobalConfig globalConfig(@Qualifier("sqlInjector") ISqlInjector sqlInjector,
                                     MetaObjectHandler metaObjectHandler) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSqlInjector(sqlInjector);
        globalConfig.setMetaObjectHandler(metaObjectHandler);
        globalConfig.setIdentifierGenerator(idGenerator());
        return globalConfig;
    }

    @Bean
    public IdentifierGenerator idGenerator() {
        return entity -> IdUtil.getId();
    }



    @Bean("batchSqlSessionFactory")
    public SqlSessionFactory batchSqlSessionFactory(@Qualifier("globalConfig") GlobalConfig globalConfig,
                                                    @Qualifier("longConnectionDataSource") DataSource longConnectionDataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(longConnectionDataSource);
        sqlSessionFactory.setGlobalConfig(globalConfig);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:**/mapper/*.xml"));
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setUseGeneratedKeys(false);
        configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
        sqlSessionFactory.setConfiguration(configuration);
        return sqlSessionFactory.getObject();
    }

}
