package ru.cubly.iopc.module.media;

import lombok.Data;
import ru.cubly.iopc.action.IntentPayload;

@Data
public class MediaControlPayload implements IntentPayload {
    private MediaAction action;
}
