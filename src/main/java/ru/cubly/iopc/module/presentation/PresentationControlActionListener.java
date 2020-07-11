package ru.cubly.iopc.module.presentation;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.cubly.iopc.action.ActionRequestedEvent;

import java.awt.*;

@Component
public class PresentationControlActionListener implements ApplicationListener<ActionRequestedEvent<PresentationControlAction>> {
    @Override
    public void onApplicationEvent(ActionRequestedEvent<PresentationControlAction> event) {
        try {
            Robot robot = new Robot();

            switch (event.getAction().getData()) {
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
    }
}
