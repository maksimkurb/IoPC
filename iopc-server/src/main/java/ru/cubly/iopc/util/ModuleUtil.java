package ru.cubly.iopc.util;

import ru.cubly.iopc.module.Module;

public class ModuleUtil {
    public static String getInputChannelName(Module m, String action) {
        return getInputChannelName(m.getModuleId(), action);
    }

    public static String getInputChannelName(String moduleId, String action) {
        return "iopc-module__" + moduleId + "_" + action;
    }

    public static String getService(Module m, String action) {
        return m.getModuleId() + "." + action;
    }

}
