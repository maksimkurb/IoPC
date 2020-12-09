package ru.cubly.iopc.util;

import ru.cubly.iopc.module.Module;

public class ModuleUtil {
    public static String getInputChannelName(Module m) {
        return "iopc-module-" + m.getModuleId();
    }

}
