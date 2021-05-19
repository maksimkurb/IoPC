package ru.cubly.iopc.util;

import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.router.HeaderValueRouter;
import ru.cubly.iopc.action.Intent;
import ru.cubly.iopc.action.IntentPayload;
import ru.cubly.iopc.module.CallableModule;
import ru.cubly.iopc.transformer.ConditionalTransformer;

import java.util.List;

import static ru.cubly.iopc.util.Constants.HEADER_SERVICE_NAME;

public class FlowUtils {
    public static IntegrationFlowBuilder forService(CallableModule module, String action) {
        return IntegrationFlows.from(ModuleUtil.getInputChannelName(module, action))
                .transform(ConditionalTransformer.ifString(Transformers.fromJson(module.getPayloadType(action))));
    }

    public static IntegrationFlow moduleRouterFlow(MessageProducerSupport messageProducer, List<CallableModule> modules, String inboundModuleId) {
        messageProducer.setErrorChannelName(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME);
        HeaderValueRouter router = new HeaderValueRouter(HEADER_SERVICE_NAME);

        router.setResolutionRequired(false);
        router.setDefaultOutputChannelName("defaultRouterChannel");

        for (CallableModule m : modules) {
            if (m.getModuleId().equals(inboundModuleId))
                continue;

            for (String action : m.getAvailableActions()) {
                String service = ModuleUtil.getService(m, action);
                router.setChannelMapping(service, ModuleUtil.getInputChannelName(m, action));
            }
        }

        return IntegrationFlows.from(messageProducer)
                .log(LoggingHandler.Level.TRACE)
                .transform(ConditionalTransformer.ifString(Transformers.fromJson(Intent.class)))
                .enrichHeaders(h -> h.headerExpression(HEADER_SERVICE_NAME, "payload.service"))
                .transform(FlowUtils::extractPayload)
                .route(router)
                .get();
    }

    private static Object extractPayload(Intent s) {
        if (s.getPayload() == null || s.getPayload().equals("null")) return IntentPayload.DUMMY;
        return s.getPayload();
    }
}
