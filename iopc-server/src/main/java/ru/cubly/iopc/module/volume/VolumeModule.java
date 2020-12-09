package ru.cubly.iopc.module.volume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.DummyIntentPayload;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.jni.VolumeControl;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.mqtt.MqttModule;
import ru.cubly.iopc.module.mqtt.MqttPayload;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class VolumeModule extends AbstractModule implements CallableModule {
    public static final String ACTION_SET = "set";
    public static final String ACTION_GET = "get";
    public static final Long DELAY_BETWEEN_SET_AND_GET = 50L;

    @Autowired
    @Lazy
    private MqttModule mqttModule;

    protected VolumeModule() {
        super("volume", Collections.singletonList(PlatformType.Windows));
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType(String action) {
        if (ACTION_SET.equals(action)) return SetVolumePayload.class;

        return DummyIntentPayload.class;
    }

    @Override
    public List<String> getAvailableActions() {
        return Arrays.asList(ACTION_SET, ACTION_GET);
    }

    @Bean
    public IntegrationFlow setVolumeIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_SET)
                .handle(this::setVolume)
                .delay("delayer.messageGroupId", d -> d.defaultDelay(DELAY_BETWEEN_SET_AND_GET))
                .channel(ModuleUtil.getInputChannelName(this, ACTION_GET))
                .get();
    }

    @Bean
    public IntegrationFlow getVolumeIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_GET)
                .handle(this::getVolume)
                .channel(ModuleUtil.getInputChannelName(mqttModule, MqttModule.ACTION_SEND))
                .get();
    }

    private Object setVolume(SetVolumePayload payload, MessageHeaders headers) {
        VolumeControl.setMasterVolume(Math.min(Math.max(payload.getVolume(), 0), 100));
        return new DummyIntentPayload();
    }

    private MqttPayload getVolume(IntentPayload payload, MessageHeaders headers) {
        return new MqttPayload("volume/level", VolumeControl.getMasterVolume());
    }
}
