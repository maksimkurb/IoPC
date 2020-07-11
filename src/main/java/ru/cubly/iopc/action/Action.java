package ru.cubly.iopc.action;

import java.util.Objects;

public abstract class Action {
    private String actionId;

    public String getActionId() {
        return actionId;
    }
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    @Override
    public String toString() {
        return "Action{" +
                "actionId='" + actionId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        Action action = (Action) o;
        return Objects.equals(actionId, action.actionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId);
    }
}
