package ru.cubly.iopc.module.power;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.DummyIntentPayload;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.PlatformType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Log4j2
public class PowerModule extends AbstractModule implements CallableModule {
    public static final String ACTION_SHUTDOWN = "shutdown";
    public static final String ACTION_REBOOT = "reboot";
    public static final String ACTION_SLEEP = "sleep";
    public static final String ACTION_HIBERNATE = "hibernate";

    protected PowerModule() {
        super("power", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
    }

    @Override
    public List<String> getAvailableActions() {
        return Arrays.asList(
                ACTION_SHUTDOWN,
                ACTION_REBOOT,
                ACTION_SLEEP,
                ACTION_HIBERNATE
        );
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType(String action) {
        return DummyIntentPayload.class;
    }

    @Bean
    public IntegrationFlow powerShutdownIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_SHUTDOWN)
                .handle(this::shutdown)
                .get();
    }

    @Bean
    public IntegrationFlow powerRebootIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_REBOOT)
                .handle(this::reboot)
                .get();
    }

    @Bean
    public IntegrationFlow powerSleepIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_SLEEP)
                .handle(this::sleep)
                .get();
    }

    @Bean
    public IntegrationFlow powerHibernateIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_HIBERNATE)
                .handle(this::hibernate)
                .get();
    }

    private void shutdown(Message<?> message) {
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            executeCommand("sudo shutdown -h now");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            executeCommand("shutdown.exe -s -t 0");
        } else {
            log.error("Could not execute power module action: IoPC does not know how to shutdown this OS");
        }
    }

    private void reboot(Message<?> message) {
        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            executeCommand("sudo shutdown -r now");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            executeCommand("shutdown.exe -r");
        } else {
            log.error("Could not execute power module action: IoPC does not know how to shutdown this OS");
        }
    }

    private void sleep(Message<?> message) {
        if (SystemUtils.IS_OS_LINUX) {
            executeCommand("sudo pm-suspend");
        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            executeCommand("sudo pmset sleepnow");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            Win32Suspend.SetSuspendState(false, true, false);
        } else {
            log.error("Could not execute power module action: IoPC does not know how to shutdown this OS");
        }
    }

    private void hibernate(Message<?> message) {
        if (SystemUtils.IS_OS_LINUX) {
            executeCommand("sudo pm-hibernate");
        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            // TODO: investigate how to put mac os into hibernation mode
            executeCommand("sudo pmset sleepnow");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            Win32Suspend.SetSuspendState(true, true, false);
        } else {
            log.error("Could not execute power module action: IoPC does not know how to shutdown this OS");
        }
    }

    private void executeCommand(String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            log.error("Failed to execute command '{}'", command, e);
        }
    }
}
