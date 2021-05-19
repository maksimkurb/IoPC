package ru.cubly.iopc.module.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.cubly.iopc.action.IntentPayload;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqttPayload implements IntentPayload {
    private String topic;
    private Object body;
    private int qos;
    private boolean retained;

    public MqttPayload(String topic, Object body) {
        this.topic = topic;
        this.body = body;
    }

    public MqttPayload(String topic, Object body, int qos) {
        this.topic = topic;
        this.body = body;
        this.qos = qos;
    }

    public MqttPayload(String topic, Object body, boolean retained) {
        this.topic = topic;
        this.body = body;
        this.retained = retained;
    }
}
