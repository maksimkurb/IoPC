package ru.cubly.iopc.module.volume;

import lombok.Data;
import ru.cubly.iopc.action.IntentPayload;

@Data
public class SetVolumePayload implements IntentPayload {
    private Integer volume;
}
