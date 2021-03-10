package ru.cubly.iopc.module.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.websocket.IntegrationWebSocketContainer;
import org.springframework.integration.websocket.ServerWebSocketContainer;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig {
    private final WebSocketProperties webSocketProperties;

    @Bean
    public IntegrationWebSocketContainer webSocketIntegrationContainer() {
        return new ServerWebSocketContainer("/websocket").withSockJs();
    }

    @Bean
    public WebSocketInboundChannelAdapter webSocketInboundChannelAdapter(IntegrationWebSocketContainer webSocketIntegrationContainer) {
        return new WebSocketInboundChannelAdapter(webSocketIntegrationContainer);
    }
}
