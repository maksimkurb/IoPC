package ru.cubly.iopc.module.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.cubly.iopc.action.IntentPayload;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqttPayload implements IntentPayload {
    private String topic;
    private Object body;
}
