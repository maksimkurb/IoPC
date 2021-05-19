package ru.cubly.iopc.module.webui.service;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.module.ConfigurableModule;
import ru.cubly.iopc.module.Module;
import ru.cubly.iopc.module.webui.model.AppConfig;
import ru.cubly.iopc.module.webui.model.ModuleDescription;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class LogsService {
    public String getAppLogs() {
        try {
            return Files.readString(Path.of("./logs/iopc.log"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}