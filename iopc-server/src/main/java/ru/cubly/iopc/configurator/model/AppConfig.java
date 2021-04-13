package ru.cubly.iopc.configurator.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AppConfig {
    @Min(1024)
    @Max(65535)
    @NotNull
    private Integer port;

    @Pattern(regexp = "en|ru")
    private String language;
}
