package ru.cubly.iopc.action;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

public class Intent {
    public Intent() {
    }

    private String module;

    @JsonDeserialize(using = IntentPayloadDeserializer.class)
    private String payload;

    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }

    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }


    @Override
    public String toString() {
        return "Intent{" +
                "module='" + module + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Intent)) return false;
        Intent intent = (Intent) o;
        return Objects.equals(module, intent.module) &&
                Objects.equals(payload, intent.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, payload);
    }
}
