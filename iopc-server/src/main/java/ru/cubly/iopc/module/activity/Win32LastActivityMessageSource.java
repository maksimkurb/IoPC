package ru.cubly.iopc.module.activity;

import org.springframework.integration.endpoint.AbstractMessageSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ru.cubly.iopc.module.activity.Win32IdleTime.getIdleTimeMillisWin32;

public class Win32LastActivityMessageSource extends AbstractMessageSource<LocalDateTime> {
    public Win32LastActivityMessageSource() {
        Win32IdleTime.init();
    }

    @Override
    protected LocalDateTime doReceive() {
        return LocalDateTime.now().minus(getIdleTimeMillisWin32(), ChronoUnit.MILLIS);
    }

    @Override
    public String getComponentType() {
        return "iopc:activity:inbound-channel-adapter";
    }
}
