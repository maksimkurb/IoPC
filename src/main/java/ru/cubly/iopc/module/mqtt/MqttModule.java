package ru.cubly.iopc.module.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.action.ActionRequestedEvent;
import ru.cubly.iopc.module.Module;
import ru.cubly.iopc.module.presentation.PresentationControlAction;
import util.PlatformType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MqttModule implements Module {
    private final ApplicationEventPublisher applicationEventPublisher;

    public MqttModule(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String getModuleId() {
        return "mqtt";
    }

    @Override
    public List<PlatformType> getAvailablePlatforms() {
        return Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS);
    }

    @Override
    public CompletableFuture<Void> start() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> stop() {
        return CompletableFuture.completedFuture(null);
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            System.out.println(message.getPayload());
            PresentationControlAction action = new PresentationControlAction();
            action.setData("next");

            applicationEventPublisher.publishEvent(new ActionRequestedEvent<>(action));
        };
    }
}
