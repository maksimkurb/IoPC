package ru.cubly.iopc.module.media;

import ru.cubly.iopc.action.IntentPayload;

public class MediaControlPayload implements IntentPayload {
    private String data;

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
}
