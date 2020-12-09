package ru.cubly.iopc.module.presentation;

import lombok.Data;
import ru.cubly.iopc.action.IntentPayload;

@Data
public class PresentationControlPayload implements IntentPayload {
    private PresentationAction action;
}
