package ru.cubly.iopc.module.keyboard;

import org.springframework.stereotype.Service;
import ru.cubly.iopc.module.Module;
import util.PlatformType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class KeyboardModule implements Module {
    @Override
    public String getModuleId() {
        return "keyboard";
    }

    @Override
    public List<PlatformType> getAvailablePlatforms() {
        return Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS);
    }

    @Override
    public CompletableFuture<Void> start() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> stop() {
        return CompletableFuture.completedFuture(null);
    }
}
