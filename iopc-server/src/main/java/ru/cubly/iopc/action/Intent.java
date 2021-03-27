package ru.cubly.iopc.action;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class Intent {
    private String service;

    @JsonDeserialize(using = IntentPayloadDeserializer.class)
    private String payload;

}
