package ru.cubly.iopc;

import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import ru.cubly.iopc.module.Module;
import ru.cubly.iopc.module.presentation.PresentationControlPayload;
import ru.cubly.iopc.transformer.ConditionalTransformer;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.util.List;

public abstract class AbstractModule implements Module {
    private final String moduleId;
    private final List<PlatformType> platforms;

    protected AbstractModule(String moduleId, List<PlatformType> platforms) {
        this.moduleId = moduleId;
        this.platforms = platforms;
    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public List<PlatformType> getAvailablePlatforms() {
        return platforms;
    }
}
