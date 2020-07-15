package ru.cubly.iopc.module.mqtt;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.action.Intent;
import ru.cubly.iopc.module.Module;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.List;

@Service
public class MqttModule implements Module {
    public MqttModule() {
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
        return IntegrationFlows.from(mqttMessageDrivenChannelAdapter)
                .log(LoggingHandler.Level.TRACE)
                .transform(Transformers.fromJson(Intent.class))
                .channel("intent")
                .get();
    }
}
