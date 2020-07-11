package ru.cubly.iopc.module.keyboard;

import ru.cubly.iopc.action.Action;

public class KeyPressAction extends Action {
    private Integer keycode;

    public Integer getKeycode() {
        return keycode;
    }

    public void setKeycode(Integer keycode) {
        this.keycode = keycode;
    }
}
