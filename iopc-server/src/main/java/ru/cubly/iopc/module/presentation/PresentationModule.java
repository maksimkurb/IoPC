package ru.cubly.iopc.module.presentation;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.keyboard.KeyboardControlPayload;
import ru.cubly.iopc.module.keyboard.KeyboardModule;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PresentationModule extends AbstractModule implements CallableModule {
    public static final String ACTION_CONTROL = "control";
    private final KeyboardModule keyboardModule;

    protected PresentationModule(KeyboardModule keyboardModule) {
        super("presentation", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
        this.keyboardModule = keyboardModule;
    }

    @Override
    public List<String> getAvailableActions() {
        return Collections.singletonList(ACTION_CONTROL);
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType(String action) {
        return PresentationControlPayload.class;
    }

    @Bean
    public IntegrationFlow presentationIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_CONTROL)
                .transform(this::transformToKeyboard)
                .channel(ModuleUtil.getInputChannelName(keyboardModule, KeyboardModule.ACTION_PRESS))
                .get();
    }

    private KeyboardControlPayload transformToKeyboard(PresentationControlPayload payload) {
        KeyboardControlPayload keyboardControlPayload = new KeyboardControlPayload();

        switch (payload.getAction()) {
            case NEXT: {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_RIGHT);
                break;
            }
            case PREVIOUS: {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_LEFT);
                break;
            }
            case START: {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_F5);
                break;
            }
            case STOP: {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_ESCAPE);
                break;
            }
            default:
                throw new IllegalArgumentException("Presentation module does not supports this action");
        }

        return keyboardControlPayload;
    }

}
