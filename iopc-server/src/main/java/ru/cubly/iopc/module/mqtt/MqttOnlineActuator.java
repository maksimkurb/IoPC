package ru.cubly.iopc.module.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static ru.cubly.iopc.module.mqtt.MqttConfig.STATE_ONLINE;
import static ru.cubly.iopc.module.mqtt.MqttConfig.STATE_QOS;
import static ru.cubly.iopc.module.mqtt.MqttUtil.STATE_TOPIC;

@Component
@ConditionalOnBean(MqttConfig.class)
@RequiredArgsConstructor
public class MqttOnlineActuator {
    private final MqttModule.MqttMessagingTemplate mqttMessagingTemplate;

    @Scheduled(fixedRate = 30000, initialDelay = 1000)
    public void sendScheduledReport() {
        mqttMessagingTemplate.send(new MqttPayload(STATE_TOPIC, STATE_ONLINE, STATE_QOS, true));
    }
}
