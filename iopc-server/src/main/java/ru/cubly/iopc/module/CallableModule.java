package ru.cubly.iopc.module;

import ru.cubly.iopc.action.IntentPayload;

import java.util.List;

public interface CallableModule extends Module {
    /**
     * Get actions that available for auto-routing.
     * Action is a string after module name in service name: <code>[moduleName].[actionName]</code>
     * e.g. <code>keyboard.press</code>
     *
     * @return List of actions
     */
    List<String> getAvailableActions();

    /**
     * Get payload class type by action
     *
     * @param action Action name
     * @return Class of payload type
     */
    Class<? extends IntentPayload> getPayloadType(String action);
}
