package ru.cubly.iopc.action;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public class ActionRequestedEvent<T extends Action> extends ApplicationEvent implements ResolvableTypeProvider {
    public ActionRequestedEvent(T source) {
        super(source);
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
    }

    public T getAction() {
        if (getSource() == null) {
            return null;
        }

        return (T)getSource();
    }
}
