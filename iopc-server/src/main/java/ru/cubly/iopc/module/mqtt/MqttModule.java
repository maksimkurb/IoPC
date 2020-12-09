package ru.cubly.iopc.module.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageProcessorSpec;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.Intent;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.transformer.ConditionalTransformer;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.cubly.iopc.util.Constants.HEADER_SERVICE_NAME;

@Service
@Slf4j
public class MqttModule extends AbstractModule implements CallableModule {
    public static final String ACTION_SEND = "send";
    private final List<CallableModule> modules;

    protected MqttModule(List<CallableModule> modules, MqttProperties mqttProperties) {
        super("mqtt", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));

        this.modules = modules;
        this.modules.remove(this);
    }

    @Override
    public List<String> getAvailableActions() {
        return Collections.singletonList(ACTION_SEND);
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType(String action) {
        return MqttPayload.class;
    }

    @Bean
    public IntegrationFlow mqttIntegrationFlow(MessageHandler mqttOutboundMessageHandler) {
        return FlowUtils.forService(this, ACTION_SEND)
                .enrichHeaders(h -> h.headerExpression(MqttHeaders.TOPIC,
                        "T(ru.cubly.iopc.module.mqtt.MqttUtil).outboundTopic(@mqttProperties.prefix, @mqttProperties.clientId, payload.topic)"))
                .transform(MqttPayload::getBody)
                .transform(ConditionalTransformer.ifNotString(Transformers.toJson()))
                .handle(mqttOutboundMessageHandler)
                .get();
    }

    @Bean
    public IntegrationFlow mqttInbound(MessageProducerSupport mqttMessageDrivenChannelAdapter) {
        mqttMessageDrivenChannelAdapter.setErrorChannelName(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME);
        HeaderValueRouter router = new HeaderValueRouter(HEADER_SERVICE_NAME);

        for (CallableModule m : modules) {
            if (m.getModuleId().equals(this.getModuleId()))
                continue;

            for (String action : m.getAvailableActions()) {
                String service = ModuleUtil.getService(m, action);
                router.setChannelMapping(service, ModuleUtil.getInputChannelName(m, action));
            }
        }

        return IntegrationFlows.from(mqttMessageDrivenChannelAdapter)
                .log(LoggingHandler.Level.TRACE)
                .transform(Transformers.fromJson(Intent.class))
                .enrichHeaders(h -> h.headerExpression(HEADER_SERVICE_NAME, "payload.service"))
                .transform(Intent::getPayload)
                .route(router)
                .get();
    }
}
