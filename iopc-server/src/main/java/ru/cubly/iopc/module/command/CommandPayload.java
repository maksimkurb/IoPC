package ru.cubly.iopc.module.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.cubly.iopc.action.IntentPayload;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandPayload implements IntentPayload {
    private String entrypointId;
    private Map<String, String> environment;
}
