package ru.cubly.iopc.module.webui;

import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;

@Service
public class WebUIModule extends AbstractModule {
    protected WebUIModule() {
        super("webui", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
    }
}
