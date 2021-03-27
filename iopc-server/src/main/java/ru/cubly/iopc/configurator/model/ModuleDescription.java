package ru.cubly.iopc.configurator.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuleDescription {
    private String moduleId;
    private String moduleName;
    private boolean enabled;

    private boolean configurable;
    private String configFragment;
    private Object moduleConfig;
}
