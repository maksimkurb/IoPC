package ru.cubly.iopc;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableScheduling
@EnableIntegration
@EnableAsync
@SpringBootApplication
@ConfigurationPropertiesScan("ru.cubly.iopc")
public class IoPCApplication {
    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(IoPCApplication.class)
                .run(args);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("IoPCAsync-");
        executor.initialize();
        return executor;
    }
}
