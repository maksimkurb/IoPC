package ru.cubly.iopc.module.activity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.module.mqtt.MqttModule;
import ru.cubly.iopc.module.mqtt.MqttPayload;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.Collections;

import static ru.cubly.iopc.module.activity.Win32IdleTime.getIdleTimeMillisWin32;
import static ru.cubly.iopc.module.mqtt.MqttModule.ACTION_SEND;

@Service
@Slf4j
public class ActivityModule extends AbstractModule {
    private final MqttModule mqttModule;
    private final MqttModule.MqttMessagingTemplate mqttMessagingTemplate;
    private ActivityState activityState = ActivityState.UNKNOWN;

    protected ActivityModule(MqttModule mqttModule,
                             MqttModule.MqttMessagingTemplate mqttMessagingTemplate) {
        super("activity", Collections.singletonList(PlatformType.Windows)
        );
        this.mqttModule = mqttModule;
        this.mqttMessagingTemplate = mqttMessagingTemplate;
    }

    @Scheduled(cron = "${activity.report-cron:-}")
    public void sendScheduledReport() {
        if (System.getProperty("os.name").contains("Windows")) {
            sendReportToMqtt();
        }
    }

    public void sendReportToMqtt() {
        int idleSec = getIdleTimeMillisWin32() / 1000;
        ActivityState newState =
                idleSec < 30 ? ActivityState.ACTIVE :
                        idleSec > 5 * 60 ? ActivityState.AWAY : ActivityState.IDLE;
        if (newState != activityState) {
            activityState = newState;
            sendMessage("activity/state", activityState.toString().toLowerCase());
        }
        // TODO: send last-will message "away"
    }

    private void sendMessage(String topic, Object value) {
        var mqttChannel = ModuleUtil.getInputChannelName(mqttModule, ACTION_SEND);
        mqttMessagingTemplate.send(new MqttPayload(topic, value));
    }
}
