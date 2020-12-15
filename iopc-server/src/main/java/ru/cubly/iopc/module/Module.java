package ru.cubly.iopc.module;

import ru.cubly.iopc.util.PlatformType;

import java.util.List;

public interface Module {
    /**
     * Get module identifier. Must be unique
     * @return module name
     */
    String getModuleId();

    /**
     * Get list of platforms, where this module can be enabled
     * @return list of supported platforms
     */
    List<PlatformType> getAvailablePlatforms();
}
