package org.ishareReading.bankai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskScheduler customThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        ScheduledThreadPoolExecutor executor = scheduler.getScheduledThreadPoolExecutor();
        executor.setMaximumPoolSize(15);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        scheduler.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("Task " + r.toString() + " rejected from " + executor.toString());
        }
    }
}    