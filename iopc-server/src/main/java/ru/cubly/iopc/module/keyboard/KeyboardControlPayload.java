package ru.cubly.iopc.module.keyboard;

import lombok.Data;
import ru.cubly.iopc.action.IntentPayload;

@Data
public class KeyboardControlPayload implements IntentPayload {
    private Integer keyCode;
}
