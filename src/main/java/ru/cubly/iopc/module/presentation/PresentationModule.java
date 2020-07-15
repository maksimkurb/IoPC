package ru.cubly.iopc.module.presentation;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Transformers;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.action.Intent;
import ru.cubly.iopc.module.Module;
import ru.cubly.iopc.util.PlatformType;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Service
public class PresentationModule implements Module {
    @Override
    public String getModuleId() {
        return "presentation";
    }

    @Override
    public List<PlatformType> getAvailablePlatforms() {
        return Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS);
    }

    @Bean
    public IntegrationFlow presentationIntegrationFlow() {
        return IntegrationFlows.from("intent")
                .log()
                .<Intent>filter(i -> this.getModuleId().equals(i.getModule()))
                .transform(Intent::getPayload)
                .transform(Transformers.fromJson(PresentationControlIntent.class))
                .<PresentationControlIntent>handle((presentationControlIntent, messageHeaders) -> {
                    System.out.printf("DATA IS == %s\n", presentationControlIntent.getData());
                    return presentationControlIntent.getData();
                })
                .<String>handle((data, h) -> {
                    try {
                        Robot robot = new Robot();

                        switch (data) {
                            case "next": {
                                robot.keyPress(39);
                                break;
                            }
                            case "prev": {
                                robot.keyPress(37);
                                break;
                            }
                            case "begin": {
                                robot.keyPress(116);
                                break;
                            }
                            case "end": {
                                robot.keyPress(27);
                                break;
                            }
                        }

                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .get();
    }
}
