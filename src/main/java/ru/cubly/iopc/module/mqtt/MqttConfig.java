package ru.cubly.iopc.module.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MqttConfig {
    private final MqttProperties properties;

    public MqttConfig(MqttProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{properties.getServerURI()});
        mqttConnectOptions.setUserName(properties.getUserName());
        mqttConnectOptions.setPassword(properties.getPassword().toCharArray());
        return mqttConnectOptions;
    }

    @Bean
    public MessageProducerSupport mqttMessageDrivenChannelAdapter(MqttConnectOptions options) {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
        clientFactory.setConnectionOptions(options);

        System.out.printf("MQTT will be listening on %s/%s/#\n", properties.getPrefix(), properties.getClientId());

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(properties.getClientId(), clientFactory,
                        properties.getPrefix()+"/"+properties.getClientId()+"/#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
}
