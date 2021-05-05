package ru.cubly.iopc.module.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.ConfigurableModule;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WebSocketModule extends AbstractModule implements ConfigurableModule<WebSocketProperties> {
    private final List<CallableModule> modules;
    private final WebSocketProperties webSocketProperties;

    protected WebSocketModule(List<CallableModule> modules, WebSocketProperties webSocketProperties) {
        super("websocket", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));

        this.modules = modules;
        this.webSocketProperties = webSocketProperties;
    }

    @Bean
    public IntegrationFlow webSocketInbound(WebSocketInboundChannelAdapter webSocketInboundChannelAdapter) {
        return FlowUtils.moduleRouterFlow(
                webSocketInboundChannelAdapter,
                modules,
                this.getModuleId()
        );
    }

    @Override
    public WebSocketProperties getConfigFragmentModel() {
        return webSocketProperties;
    }

    @Override
    public Map<String, String> buildConfigProperties(WebSocketProperties model) {
        HashMap<String, String> properties = new HashMap<>();

        properties.put("websocket.secured", model.getSecured().toString());
        properties.put("websocket.username", model.getUsername());
        properties.put("websocket.password", model.getPassword());

        return properties;
    }
}
