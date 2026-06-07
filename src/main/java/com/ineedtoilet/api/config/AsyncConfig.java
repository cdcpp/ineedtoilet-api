package com.ineedtoilet.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "excelParsingExecutor")
    public Executor excelParsingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();


        executor.setCorePoolSize(2);
        executor.setQueueCapacity(10);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("Excel-Worker-");

        executor.initialize();
        return executor;
    }
}