package ru.cubly.iopc.module.websocket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "websocket")
@Data
public class WebSocketProperties {
    private Boolean secured = false;
    private String username;
    private String password;
}
