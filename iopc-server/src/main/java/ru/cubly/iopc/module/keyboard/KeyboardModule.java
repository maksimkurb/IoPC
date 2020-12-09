package ru.cubly.iopc.module.keyboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.transformer.ConditionalTransformer;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.awt.*;
import java.util.Arrays;


@Service
@Slf4j
public class KeyboardModule extends AbstractModule implements CallableModule {
    private final Robot robot;

    public KeyboardModule() {
        super("keyboard", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));

        Robot robot;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            robot = null;
            log.error("Failed to initialize a robot", e);
        }
        this.robot = robot;
    }

    @Bean
    public IntegrationFlow keyboardIntegrationFlow() {
        return IntegrationFlows.from(ModuleUtil.getInputChannelName(this))
                .transform(ConditionalTransformer.ifString(Transformers.fromJson(KeyboardControlPayload.class)))
                .handle(this::handle)
                .get();
    }

    private Object handle(KeyboardControlPayload payload, MessageHeaders h) {
        if (robot == null) {
            log.info("Skipping KeyPress instruction because AWT robot is failed to instantiate");
            return null;
        }

        robot.keyPress(payload.getKeyCode());
        return null;
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType() {
        return KeyboardControlPayload.class;
    }
}
