package ru.cubly.iopc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import ru.cubly.iopc.util.Constants;

@Configuration
public class FlowConfig {
    @Bean
    public IntegrationFlow loggingFlow() {
        return IntegrationFlows.from("defaultRouterChannel")
                .log(LoggingHandler.Level.WARN, "ROUTER_LOGGER",
                        m -> "Route not found for message: service="
                                + m.getHeaders().getOrDefault(Constants.HEADER_SERVICE_NAME, "[null]")
                                + " message_id=" + m.getHeaders().getId()
                                + " payload=" + m.getPayload()
                )
                .get();
    }

}
