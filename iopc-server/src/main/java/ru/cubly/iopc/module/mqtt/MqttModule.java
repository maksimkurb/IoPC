package ru.cubly.iopc.module.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.action.Intent;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.Module;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.List;

import static ru.cubly.iopc.util.Constants.HEADER_MODULE_NAME;

@Service
@Slf4j
public class MqttModule implements Module {
    private final List<CallableModule> modules;

    public MqttModule(List<CallableModule> modules) {
        this.modules = modules;
    }

    @Override
    public String getModuleId() {
        return "mqtt";
    }

    @Override
    public List<PlatformType> getAvailablePlatforms() {
        return Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS);
    }

    @Bean
    public IntegrationFlow mqttInbound(MessageProducerSupport mqttMessageDrivenChannelAdapter) {
        mqttMessageDrivenChannelAdapter.setErrorChannelName(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME);
        HeaderValueRouter router = new HeaderValueRouter(HEADER_MODULE_NAME);

        for (CallableModule m : modules) {
            router.setChannelMapping(m.getModuleId(), ModuleUtil.getInputChannelName(m));
        }

        return IntegrationFlows.from(mqttMessageDrivenChannelAdapter)
                .log(LoggingHandler.Level.TRACE)
                .transform(Transformers.fromJson(Intent.class))
                .enrichHeaders(h -> h.headerExpression(HEADER_MODULE_NAME, "payload.module"))
                .transform(Intent::getPayload)
                .route(router)
                .get();
    }
}
