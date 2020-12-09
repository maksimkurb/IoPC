package ru.cubly.iopc.module;

import ru.cubly.iopc.action.IntentPayload;

public interface CallableModule extends Module {
    Class<? extends IntentPayload> getPayloadType();
}
