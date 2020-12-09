package ru.cubly.iopc.module.presentation;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.keyboard.KeyboardControlPayload;
import ru.cubly.iopc.module.keyboard.KeyboardModule;
import ru.cubly.iopc.transformer.ConditionalTransformer;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.awt.event.KeyEvent;
import java.util.Arrays;

@Service
public class PresentationModule extends AbstractModule implements CallableModule {
    private final KeyboardModule keyboardModule;

    protected PresentationModule(KeyboardModule keyboardModule) {
        super("presentation", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
        this.keyboardModule = keyboardModule;
    }

    @Bean
    public IntegrationFlow presentationIntegrationFlow() {
        return IntegrationFlows.from(ModuleUtil.getInputChannelName(this))
                .transform(ConditionalTransformer.ifString(Transformers.fromJson(PresentationControlPayload.class)))
                .transform(this::transformToKeyboard)
                .channel(ModuleUtil.getInputChannelName(keyboardModule))
                .get();
    }

    private KeyboardControlPayload transformToKeyboard(PresentationControlPayload payload) {
        KeyboardControlPayload keyboardControlPayload = new KeyboardControlPayload();

        switch (payload.getData()) {
            case "next": {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_RIGHT);
                break;
            }
            case "previous": {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_LEFT);
                break;
            }
            case "begin": {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_F5);
                break;
            }
            case "end": {
                keyboardControlPayload.setKeyCode(KeyEvent.VK_ESCAPE);
                break;
            }
            default:
                throw new IllegalArgumentException("Presentation module does not supports this action");
        }

        return keyboardControlPayload;
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType() {
        return PresentationControlPayload.class;
    }
}
