package ru.cubly.iopc.module.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.integration.websocket.inbound.WebSocketInboundChannelAdapter;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.Intent;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.List;

import static ru.cubly.iopc.util.Constants.HEADER_SERVICE_NAME;

@Service
@Slf4j
public class WebSocketModule extends AbstractModule {
    private final List<CallableModule> modules;

    protected WebSocketModule(List<CallableModule> modules) {
        super("websocket", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));

        this.modules = modules;
    }

    @Bean
    public IntegrationFlow webSocketInbound(WebSocketInboundChannelAdapter webSocketInboundChannelAdapter) {
        webSocketInboundChannelAdapter.setErrorChannelName(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME);
        HeaderValueRouter router = new HeaderValueRouter(HEADER_SERVICE_NAME);

        for (CallableModule m : modules) {
            if (m.getModuleId().equals(this.getModuleId()))
                continue;

            for (String action : m.getAvailableActions()) {
                String service = ModuleUtil.getService(m, action);
                router.setChannelMapping(service, ModuleUtil.getInputChannelName(m, action));
            }
        }

        return IntegrationFlows.from(webSocketInboundChannelAdapter)
                .log(LoggingHandler.Level.TRACE)
                .transform(Transformers.fromJson(Intent.class))
                .enrichHeaders(h -> h.headerExpression(HEADER_SERVICE_NAME, "payload.service"))
                .transform(Intent::getPayload)
                .route(router)
                .get();
    }
}
