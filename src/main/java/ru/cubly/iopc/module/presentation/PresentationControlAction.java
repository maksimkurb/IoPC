package ru.cubly.iopc.module.presentation;

import ru.cubly.iopc.action.Action;

public class PresentationControlAction extends Action {
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private String data;
}
