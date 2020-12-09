package ru.cubly.iopc.util;

import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.transformer.ConditionalTransformer;

public class FlowUtils {
    public static IntegrationFlowBuilder forService(CallableModule module, String action) {
        return IntegrationFlows.from(ModuleUtil.getInputChannelName(module, action))
                .transform(ConditionalTransformer.ifString(Transformers.fromJson(module.getPayloadType(action))));
    }
}
