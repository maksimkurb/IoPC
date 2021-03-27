package ru.cubly.iopc.module.mqtt;

public class MqttUtil {
    public static String outboundTopic(String prefix, String clientId, String topic) {
        return prefix + "/" + clientId + "/" + topic;
    }
}
