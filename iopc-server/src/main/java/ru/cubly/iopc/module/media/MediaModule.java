package ru.cubly.iopc.module.media;

import lombok.extern.log4j.Log4j2;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.volume.VolumeModule;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class MediaModule extends AbstractModule implements CallableModule {
    private static final String ACTION_CONTROL = "control";

    protected MediaModule() {
        super("media", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
    }

    @Autowired
    private VolumeModule volumeModule;

    @Override
    public List<String> getAvailableActions() {
        return Collections.singletonList(ACTION_CONTROL);
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType(String action) {
        return MediaControlPayload.class;
    }

    @Bean
    public IntegrationFlow mediaIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_CONTROL)
                .handle(this::handle)
                .delay("delayer.messageGroupId", d -> d.defaultDelay(VolumeModule.DELAY_BETWEEN_SET_AND_GET))
                .channel(ModuleUtil.getInputChannelName(volumeModule, VolumeModule.ACTION_GET))
                .get();
    }

    private Object handle(MediaControlPayload payload, MessageHeaders header) {
        log.debug("Simulating pressing virtual key {}", payload.getAction());

        switch (payload.getAction()) {
            case PLAY_PAUSE: {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 179, NativeKeyEvent.VC_MEDIA_PLAY));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 179, NativeKeyEvent.VC_MEDIA_PLAY));
                break;
            }
            case VOLUME_UP: {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 175, NativeKeyEvent.VC_VOLUME_UP));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 175, NativeKeyEvent.VC_VOLUME_UP));
                return IntentPayload.DUMMY;
            }
            case VOLUME_DOWN: {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 174, NativeKeyEvent.VC_VOLUME_DOWN));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 174, NativeKeyEvent.VC_VOLUME_DOWN));
                return IntentPayload.DUMMY;
            }
            case MUTE: {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 173, NativeKeyEvent.VC_VOLUME_MUTE));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 173, NativeKeyEvent.VC_VOLUME_MUTE));
                return IntentPayload.DUMMY;
            }
            case PREVIOUS: {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 177, NativeKeyEvent.VC_MEDIA_PREVIOUS));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 177, NativeKeyEvent.VC_MEDIA_PREVIOUS));
                break;
            }
            case NEXT: {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 176, NativeKeyEvent.VC_MEDIA_NEXT));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 176, NativeKeyEvent.VC_MEDIA_NEXT));
                break;
            }
            case STOP: {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 178, NativeKeyEvent.VC_MEDIA_STOP));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 178, NativeKeyEvent.VC_MEDIA_STOP));
                break;
            }
            default:
                throw new IllegalArgumentException("Media Control module does not supports this action");
        }

        return null;
    }

    private NativeKeyEvent buildKeyEvent(int event, int rawCode, int keyCode) {
        return new NativeKeyEvent(event, 0, rawCode, keyCode, NativeKeyEvent.CHAR_UNDEFINED, NativeKeyEvent.KEY_LOCATION_STANDARD);
    }
}
