package org.ishare.oss;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StartEventListener implements ApplicationListener<ApplicationStartedEvent>, Ordered {


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();
        String appName = Objects.requireNonNull(environment.getProperty("spring.application.name")).toUpperCase();
        String oss = String.format("---[%s]---启动完成，OSS自动配置成功[%s]---", appName, "SUCCESS");
        System.out.println(oss);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
