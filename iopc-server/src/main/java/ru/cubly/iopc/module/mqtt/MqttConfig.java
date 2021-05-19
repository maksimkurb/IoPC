package ru.cubly.iopc.module.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class MqttConfig {
    public static final int STATE_QOS = 1;
    public static final String STATE_ONLINE = "online";
    public static final String STATE_OFFLINE = "offline";
    private final MqttProperties mqttProperties;


    public MqttConfig(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttProperties.getServerUri()});
        options.setUserName(mqttProperties.getUsername());
        options.setPassword(mqttProperties.getPassword().toCharArray());
        options.setWill(MqttUtil.stateTopic(mqttProperties.getPrefix(), mqttProperties.getClientId()),
                STATE_OFFLINE.getBytes(StandardCharsets.UTF_8), STATE_QOS, true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageProducerSupport mqttMessageDrivenChannelAdapter(MqttPahoClientFactory clientFactory) {
        String topic = mqttProperties.getPrefix() + "/" + mqttProperties.getClientId() + "/command/#";
        log.info("MQTT is listening on {}", topic);

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId() + "-in", clientFactory, topic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public MessageHandler mqttOutboundMessageHandler(MqttPahoClientFactory clientFactory) {
        String topic = MqttUtil.outboundTopic(mqttProperties.getPrefix(), mqttProperties.getClientId(), "default_topic");
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttProperties.getClientId() + "-out", clientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(topic);
        return messageHandler;
    }
}
