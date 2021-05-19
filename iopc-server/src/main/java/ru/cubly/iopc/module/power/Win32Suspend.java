package ru.cubly.iopc.module.power;

import com.sun.jna.Native;
import com.sun.jna.Platform;

public class Win32Suspend {
    public static native boolean SetSuspendState(boolean hibernate, boolean forceCritical, boolean disableWakeEvent);

    static {
        if (Platform.isWindows())
            Native.register(Win32Suspend.class, "powrprof");
    }
}