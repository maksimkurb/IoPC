package ru.cubly.iopc.module.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "mqtt")
@Data
public class MqttProperties {
    private String serverUri;
    private String username;
    private String password;
    private String clientId;
    @NotEmpty
    private String prefix = "iopc";
}
