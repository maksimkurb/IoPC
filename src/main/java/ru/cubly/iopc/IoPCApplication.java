package ru.cubly.iopc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan("ru.cubly.iopc")
public class IoPCApplication {
    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(IoPCApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
