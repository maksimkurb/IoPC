package ru.cubly.iopc.module.presentation;

import ru.cubly.iopc.action.IntentPayload;

public class PresentationControlPayload implements IntentPayload {
    private String data;

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}
