package ru.cubly.iopc.transformer;

import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

import java.util.function.Predicate;

public class ConditionalTransformer extends AbstractTransformer {
    private final Predicate<Message<?>> condition;
    private final Transformer transformer;

    public ConditionalTransformer(Predicate<Message<?>> condition, Transformer transformer) {
        this.condition = condition;
        this.transformer = transformer;
    }

    @Override
    protected Object doTransform(Message<?> message) {
        if (condition.test(message)) return transformer.transform(message);
        return message;
    }

    public static Transformer ifString(Transformer transformer) {
        return new ConditionalTransformer(m -> m.getPayload() instanceof String, transformer);
    }
}
