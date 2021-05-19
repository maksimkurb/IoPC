package ru.cubly.iopc.module.mqtt;

public class MqttUtil {
    public static final String STATE_TOPIC = "state";

    public static String outboundTopic(String prefix, String clientId, String topic) {
        return prefix + "/" + clientId + "/" + topic;
    }

    public static String stateTopic(String prefix, String clientId) {
        return outboundTopic(prefix, clientId, STATE_TOPIC);
    }
}
