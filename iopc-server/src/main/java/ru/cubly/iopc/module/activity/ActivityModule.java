package ru.cubly.iopc.module.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.support.json.JsonObjectMapper;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.module.mqtt.MqttModule;
import ru.cubly.iopc.module.mqtt.MqttPayload;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;
import ru.cubly.iopc.util.conditions.ConditionalOnPlatform;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Service
@Slf4j
public class ActivityModule extends AbstractModule {
    private final MqttModule mqttModule;

    protected ActivityModule(MqttModule mqttModule) {
        super("activity", Collections.singletonList(PlatformType.Windows));
        this.mqttModule = mqttModule;
    }

    @Bean
    @ConditionalOnProperty("activity.report-cron")
    @ConditionalOnPlatform({PlatformType.Windows})
    public IntegrationFlow activityPollingFlow(
            @Value("${activity.report-cron:-}") String cronExpression,
            MessageSource<LocalDateTime> activityStateMessageSource,
            JsonObjectMapper<?, ?> jsonObjectMapper
    ) {
        return IntegrationFlows.from(activityStateMessageSource,
                c -> c.poller(Pollers.cron(cronExpression).maxMessagesPerPoll(1)))
                .routeToRecipients(r -> r
                        .recipientFlow(f -> f
                                .<LocalDateTime, MqttPayload>transform(la -> new MqttPayload("activity/lastUserInput", la))
                                .transform(Transformers.toJson(jsonObjectMapper))
                                .channel(ModuleUtil.getInputChannelName(mqttModule, MqttModule.ACTION_SEND))
                        )
                        .recipientFlow(f -> f
                                .<LocalDateTime, MqttPayload>transform(la -> new MqttPayload("activity/state", getState(la)))
                                .channel(ModuleUtil.getInputChannelName(mqttModule, MqttModule.ACTION_SEND))
                        )
                )
                .get();
    }

    private ActivityState getState(LocalDateTime lastUserInput) {
        LocalDateTime now = LocalDateTime.now();
        if (lastUserInput.plus(30, ChronoUnit.SECONDS).isAfter(now)) return ActivityState.ACTIVE;
        if (lastUserInput.plus(5, ChronoUnit.MINUTES).isAfter(now)) return ActivityState.IDLE;
        return ActivityState.AWAY;
    }

    @Bean
    @ConditionalOnPlatform({PlatformType.Windows})
    public MessageSource<LocalDateTime> activityStateMessageSource() {
        return new Win32LastActivityMessageSource();
    }
}
