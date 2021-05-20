package ru.cubly.iopc.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Component
@Log4j2
public class IoPCTrayIcon extends TrayIcon {

    private static final String IMAGE_PATH = "/static/icon/icon16.png";
    private static final String TOOLTIP = "IoPC Agent";

    private final PopupMenu popup;
    private final SystemTray tray;

    private final String version;
    private final Integer port;
    private final RestartEndpoint restartEndpoint;
    private final ApplicationContext appContext;

    public IoPCTrayIcon(
            @Value("${app.build-info.version}") String version,
            @Value("${server.port}") Integer port,
            RestartEndpoint restartEndpoint, ApplicationContext appContext) {
        super(createImage(IMAGE_PATH, TOOLTIP), TOOLTIP);

        this.version = version;
        this.port = port;
        this.restartEndpoint = restartEndpoint;
        this.appContext = appContext;

        popup = new PopupMenu();
        tray = SystemTray.getSystemTray();
    }

    @PostConstruct
    private void setup() throws AWTException {
        if (!SystemTray.isSupported()) {
            log.warn("Tray is not supported in this environment");
            return;
        }

        MenuItem aboutItem = new MenuItem("IoPC v" + version + " is running");
        aboutItem.setEnabled(false);

        MenuItem configuratorItem = new MenuItem("Preferences");
        MenuItem restartItem = new MenuItem("Restart");
        MenuItem exitItem = new MenuItem("Exit");

        configuratorItem.addActionListener(e -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("http://127.0.0.1:" + port));
                } catch (IOException | URISyntaxException exception) {
                    log.error("Could not open browser", exception);
                }
            } else {
                log.error("Could not open browser, this feature is not supported in this environment");
            }
        });
        restartItem.addActionListener(e -> {
            restartEndpoint.restart();
            configuratorItem.setEnabled(false);
            restartItem.setEnabled(false);
            restartItem.setLabel("Restarting...");
            exitItem.setEnabled(false);
        });
        exitItem.addActionListener(e -> {
            SpringApplication.exit(appContext, () -> 0);
            configuratorItem.setEnabled(false);
            restartItem.setEnabled(false);
            exitItem.setEnabled(false);
            exitItem.setLabel("Exiting...");
        });

        popup.add(aboutItem);
        popup.add(configuratorItem);
        popup.add(restartItem);
        popup.add(exitItem);

        // popup.add(itemAbout);
        // here add the items to your popup menu. These extend MenuItem
        // popup.addSeparator();
        setPopupMenu(popup);
        tray.add(this);
    }

    @PreDestroy
    public void destroy() {
        if (!SystemTray.isSupported()) {
            return;
        }

        tray.remove(this);
    }

    protected static Image createImage(String path, String description) {
        URL imageURL = IoPCTrayIcon.class.getResource(path);
        return new ImageIcon(imageURL, description).getImage();
    }
}
