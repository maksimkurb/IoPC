package ru.cubly.iopc.module.media;

import lombok.extern.slf4j.Slf4j;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.jni.MediaKeys;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.keyboard.KeyboardControlPayload;
import ru.cubly.iopc.module.keyboard.KeyboardModule;
import ru.cubly.iopc.transformer.ConditionalTransformer;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;

@Service
@Slf4j
public class MediaModule extends AbstractModule implements CallableModule {
    private final KeyboardModule keyboardModule;

    protected MediaModule(KeyboardModule keyboardModule) {
        super("media", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
        this.keyboardModule = keyboardModule;

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Bean
    public IntegrationFlow mediaIntegrationFlow() {
        return IntegrationFlows.from(ModuleUtil.getInputChannelName(this))
                .transform(ConditionalTransformer.ifString(Transformers.fromJson(MediaControlPayload.class)))
                .handle(this::handle)
                .get();
    }

    private Object handle(MediaControlPayload payload, MessageHeaders header) {
        switch (payload.getData()) {
            case "pause": {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 179, NativeKeyEvent.VC_MEDIA_PLAY));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 179, NativeKeyEvent.VC_MEDIA_PLAY));
                break;
            }
            case "volumeup": {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 175, NativeKeyEvent.VC_VOLUME_UP));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 175, NativeKeyEvent.VC_VOLUME_UP));
                break;
            }
            case "volumedown": {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 174, NativeKeyEvent.VC_VOLUME_DOWN));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 174, NativeKeyEvent.VC_VOLUME_DOWN));
                break;
            }
            case "mute": {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 173, NativeKeyEvent.VC_VOLUME_MUTE));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 173, NativeKeyEvent.VC_VOLUME_MUTE));
                break;
            }
            case "prev": {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 177, NativeKeyEvent.VC_MEDIA_PREVIOUS));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 177, NativeKeyEvent.VC_MEDIA_PREVIOUS));
                break;
            }
            case "next": {
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 176, NativeKeyEvent.VC_MEDIA_NEXT));
                GlobalScreen.postNativeEvent(buildKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 176, NativeKeyEvent.VC_MEDIA_NEXT));
                break;
            }
            case "stop": {
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

    @Override
    public Class<? extends IntentPayload> getPayloadType() {
        return MediaControlPayload.class;
    }
}
