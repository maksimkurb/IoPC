package ru.cubly.iopc.module;

import util.PlatformType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Module {
    String getModuleId();
    List<PlatformType> getAvailablePlatforms();

    CompletableFuture<Void> start();
    CompletableFuture<Void> stop();
}
