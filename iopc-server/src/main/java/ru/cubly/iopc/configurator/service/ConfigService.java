package ru.cubly.iopc.configurator.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.configurator.model.AppConfig;
import ru.cubly.iopc.configurator.model.ModuleDescription;
import ru.cubly.iopc.module.ConfigurableModule;
import ru.cubly.iopc.module.Module;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigService {

    private final String iopcPropertiesFile;
    private final ServerProperties serverProperties;
    private final List<Module> modules;

    private AppConfig appConfig = null;
    private final Map<String, Object> updatedModuleConfig = new HashMap<>();

    @Getter
    private boolean reloadRequired = false;

    public ConfigService(
            @Value("${spring.config.import}") String iopcPropertiesFile,
            ServerProperties serverProperties,
            List<Module> modules) {
        this.serverProperties = serverProperties;
        this.modules = modules;
        this.iopcPropertiesFile = iopcPropertiesFile;

    }

    public AppConfig getAppConfig() {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.setPort(serverProperties.getPort());
        }

        return appConfig;
    }

    public void saveAppConfig(AppConfig config) throws IOException {
        this.reloadRequired = true;

        this.appConfig = config;

        applyProperties(Collections.singletonMap("server.port", appConfig.getPort().toString()));
    }

    public Object getEmptyModuleConfig(String moduleName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ConfigurableModule<?> configurableModule = getConfigurableModule(moduleName);

        Class<?> aClass = configurableModule.getConfigFragmentModel().getClass();

        Constructor<?> constructor = aClass.getConstructor();
        return constructor.newInstance();
    }

    public <T> void saveModuleConfig(String moduleName, T moduleConfig) throws IOException {
        ConfigurableModule<T> configurableModule = getConfigurableModule(moduleName);

        this.reloadRequired = true;

        Map<String, String> map = configurableModule.buildConfigProperties(moduleConfig);

        applyProperties(map);

        updatedModuleConfig.put(moduleName, moduleConfig);
    }

    private void applyProperties(Map<String, String> properties) throws IOException {
        String filePath = iopcPropertiesFile.replaceFirst("^(optional:)?(file:)?", "");

        Properties props = new Properties();
        try (var fis = new FileInputStream(filePath)) {
            props.load(fis);
        } catch (IOException ignore) {
        }

        props.putAll(properties);

        try (var fos = new FileOutputStream(filePath)) {
            props.store(fos, "Config edited from UI");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ConfigurableModule<T> getConfigurableModule(String moduleName) {
        Module module = modules
                .stream()
                .filter(m -> m.getModuleId().equals(moduleName))
                .findFirst()
                .orElse(null);

        if (module == null) {
            throw new IllegalArgumentException("Module with such name is not found");
        }

        if (!(module instanceof ConfigurableModule)) {
            throw new IllegalArgumentException("This module can not be configured");
        }

        return (ConfigurableModule<T>) module;
    }

    public List<ModuleDescription> getModuleDescriptions() {
        return modules
                .stream()
                .map(this::buildModuleDescription)
                .sorted(Comparator.comparing(ModuleDescription::getModuleName))
                .collect(Collectors.toUnmodifiableList());
    }

    private ModuleDescription buildModuleDescription(Module module) {
        ModuleDescription.ModuleDescriptionBuilder builder = ModuleDescription.builder();

        String moduleName = module.getClass().getSimpleName();
        if (moduleName.contains("$")) {
            moduleName = moduleName.substring(0, moduleName.indexOf('$'));
        }
        builder
                .moduleId(module.getModuleId())
                .moduleName(moduleName)
                .enabled(true);

        if (module instanceof ConfigurableModule) {
            ConfigurableModule<?> configurableModule = (ConfigurableModule<?>) module;

            Object moduleConfig = updatedModuleConfig
                    .getOrDefault(module.getModuleId(), configurableModule.getConfigFragmentModel());
            builder
                    .configurable(true)
                    .configFragment(configurableModule.getConfigFragmentName())
                    .moduleConfig(moduleConfig);
        }

        return builder.build();
    }
}