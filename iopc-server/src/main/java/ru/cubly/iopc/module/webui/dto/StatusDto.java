package ru.cubly.iopc.module.webui.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusDto {
    private boolean running;
}
