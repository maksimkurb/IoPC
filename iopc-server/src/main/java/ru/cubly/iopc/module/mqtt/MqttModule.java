package ru.cubly.iopc.module.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.ConfigurableModule;
import ru.cubly.iopc.transformer.ConditionalTransformer;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RefreshScope
@Service
@Slf4j
public class MqttModule extends AbstractModule implements CallableModule, ConfigurableModule<MqttProperties> {
    public static final String ACTION_SEND = "send";
    private final List<CallableModule> modules;
    private final MqttProperties mqttProperties;

    protected MqttModule(List<CallableModule> modules, MqttProperties mqttProperties) {
        super("mqtt", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));

        this.modules = modules;
        this.mqttProperties = mqttProperties;
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
    public IntegrationFlow mqttInbound(MessageProducerSupport mqttMessageDrivenChannelAdapter) {
        return FlowUtils.moduleRouterFlow(
                mqttMessageDrivenChannelAdapter,
                modules,
                this.getModuleId()
        );
    }

    @Component
    @MessagingGateway(defaultRequestChannel = "#{T(ru.cubly.iopc.util.ModuleUtil).getInputChannelName(\"mqtt\", \"send\")}")
    public interface MqttMessagingTemplate {
        void send(MqttPayload payload);
    }

    @Bean
    public IntegrationFlow mqttOutbound(MessageHandler mqttOutboundMessageHandler) {
        return FlowUtils.forService(this, ACTION_SEND)
                .enrichHeaders(h -> h.headerExpression(MqttHeaders.TOPIC,
                        "T(ru.cubly.iopc.module.mqtt.MqttUtil).outboundTopic(@mqttProperties.prefix, @mqttProperties.clientId, payload.topic)"))
                .transform(MqttPayload::getBody)
                .transform(ConditionalTransformer.ifNotString(Transformers.toJson()))
                .handle(mqttOutboundMessageHandler)
                .get();
    }

    @Override
    public MqttProperties getConfigFragmentModel() {
        return mqttProperties;
    }

    @Override
    public Map<String, String> buildConfigProperties(MqttProperties model) {
        HashMap<String, String> properties = new HashMap<>();

        properties.put("mqtt.server-uri", model.getServerUri());
        properties.put("mqtt.username", model.getUsername());
        properties.put("mqtt.password", model.getPassword());
        properties.put("mqtt.clientId", model.getClientId());
        properties.put("mqtt.prefix", model.getPrefix());

        return properties;
    }
}
