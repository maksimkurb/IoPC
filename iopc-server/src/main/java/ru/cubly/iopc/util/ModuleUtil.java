package ru.cubly.iopc.util;

import ru.cubly.iopc.module.Module;

public class ModuleUtil {
    public static String getInputChannelName(Module m, String action) {
        return "iopc-module__" + m.getModuleId() + "_" + action;
    }

    public static String getService(Module m, String action) {
        return m.getModuleId() + "." + action;
    }

}
