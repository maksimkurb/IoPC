package ru.cubly.iopc.module.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.module.ConfigurableModule;
import ru.cubly.iopc.util.FlowUtils;
import ru.cubly.iopc.util.PlatformType;
import ru.cubly.iopc.util.StreamUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommandModule extends AbstractModule implements CallableModule, ConfigurableModule<CommandProperties> {
    public static final String ACTION_EXECUTE = "execute";

    private final CommandProperties commandProperties;
    private final Predicate<String> validEnvironmentKey = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*").asMatchPredicate();

    protected CommandModule(CommandProperties commandProperties) {
        super("command", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
        this.commandProperties = commandProperties;
    }

    @Override
    public List<String> getAvailableActions() {
        return Collections.singletonList(
                ACTION_EXECUTE
        );
    }

    @Override
    public Class<? extends IntentPayload> getPayloadType(String action) {
        return CommandPayload.class;
    }

    @Bean
    public IntegrationFlow commandExecuteIntegrationFlow() {
        return FlowUtils.forService(this, ACTION_EXECUTE)
                .handle(this::executeCommand)
                .get();
    }

    private Object executeCommand(CommandPayload payload, MessageHeaders h) {
        if (commandProperties.getAllowedEntrypoints().isEmpty()) {
            log.warn("No allowed command entrypoints are configured. Please configure CommandModule.");
            return null;
        }
        Optional<EntrypointProperties> entrypoint = commandProperties.getAllowedEntrypoints()
                .stream()
                .filter(e -> payload.getEntrypointId().equals(e.getId()))
                .findFirst();

        if (entrypoint.isEmpty()) {
            log.error("No entrypoint with entrypoint_id={} is configured. Please check CommandModule configuration.", payload.getEntrypointId());
            return null;
        }

        try {
            log.info("Executing external command with entypoint_id={}", payload.getEntrypointId());

            String[] env = Optional.ofNullable(payload.getEnvironment())
                    .map(environment -> environment.entrySet()
                            .stream()
                            .map(e -> {
                                if (e.getKey() == null || !validEnvironmentKey.test(e.getKey())) {
                                    throw new IllegalArgumentException("Invalid environment key: \"" + e.getKey() + "\"");
                                }
                                return e.getKey() + '=' + e.getValue();
                            })
                            .toArray(String[]::new))
                    .orElse(null);

            // Using array to prevent arguments injection
            Runtime.getRuntime().exec(
                    entrypoint.get().getExecutablePath(),
                    env
            );
        } catch (IOException e) {
            log.error("Failed to execute command '{}'", entrypoint, e);
        }
        return null;
    }

    @Override
    public CommandProperties getConfigFragmentModel() {
        return commandProperties;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String, String> buildConfigProperties(CommandProperties model) {
        java.util.Properties props = new java.util.Properties();

        AtomicInteger index = new AtomicInteger(0);

        int originalCount = model.getAllowedEntrypoints().size();

        model.setAllowedEntrypoints(model.getAllowedEntrypoints().stream()
                .filter(entrypoint -> !entrypoint.getId().isEmpty() && !entrypoint.getExecutablePath().isEmpty())
                .filter(StreamUtils.distinctByKey(EntrypointProperties::getId))
                .collect(Collectors.toList()));

        model.getAllowedEntrypoints()
                .forEach(entrypoint -> {
                    props.setProperty("command.allowed-entrypoints[" + index.get() + "].id", entrypoint.getId());
                    props.setProperty("command.allowed-entrypoints[" + index.getAndIncrement() + "].executable-path", entrypoint.getExecutablePath());
                });

        Map<String, String> map = new HashMap<>((Map) props);

        for (int i = model.getAllowedEntrypoints().size(); i < originalCount; i++) {
            map.put("command.allowed-entrypoints[" + index.get() + "].id", null);
            map.put("command.allowed-entrypoints[" + index.getAndIncrement() + "].executable-path", null);
        }


        return map;
    }
}
