package ru.cubly.iopc.module.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mqtt")
@Data
public class MqttProperties {
    private String serverURI;
    private String userName;
    private String password;
    private String clientId;
    private String prefix = "iopc";
}
