package ru.cubly.iopc.module.command;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "command")
@Data
public class CommandProperties {
    private List<EntrypointProperties> allowedEntrypoints = new ArrayList<>();
}
