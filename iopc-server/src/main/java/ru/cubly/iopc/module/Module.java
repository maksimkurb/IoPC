package ru.cubly.iopc.module;

import ru.cubly.iopc.util.PlatformType;

import java.util.List;

public interface Module {
    String getModuleId();
    List<PlatformType> getAvailablePlatforms();
}
