package ru.cubly.iopc.module.sysinfo;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.module.mqtt.MqttModule;
import ru.cubly.iopc.module.mqtt.MqttPayload;
import ru.cubly.iopc.module.sysinfo.dto.DriveInfo;
import ru.cubly.iopc.util.ModuleUtil;
import ru.cubly.iopc.util.PlatformType;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import static ru.cubly.iopc.module.mqtt.MqttModule.ACTION_SEND;

@Service
public class SysInfoModule extends AbstractModule {
    private final MqttModule mqttModule;
    private final MqttModule.MqttMessagingTemplate mqttMessagingTemplate;
    private final GlobalMemory globalMemory;

    protected SysInfoModule(MqttModule mqttModule,
                            MqttModule.MqttMessagingTemplate mqttMessagingTemplate) {
        super("sysinfo", Arrays.asList(
                PlatformType.Windows,
                PlatformType.Linux,
                PlatformType.MacOS)
        );
        this.mqttModule = mqttModule;
        this.mqttMessagingTemplate = mqttMessagingTemplate;

        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        this.globalMemory = hal.getMemory();
    }

    @Scheduled(cron = "${sysinfo.report-cron:-}")
    public void sendScheduledReport() {
        sendReportToMqtt();
    }

    public void sendReportToMqtt() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        sendMessage("sysinfo/ram/free", this.globalMemory.getAvailable());
        sendMessage("sysinfo/ram/total", this.globalMemory.getTotal());
        sendMessage("sysinfo/cpu/usage", Math.round(operatingSystemMXBean.getSystemCpuLoad() * 100));

        File[] roots = File.listRoots();
        for (int i = 0; i < roots.length; i++) {
            var root = roots[i];
            sendMessage("sysinfo/drive/" + i,
                    DriveInfo.builder()
                            .path(root.getAbsolutePath())
                            .freeSpace(root.getFreeSpace())
                            .totalSpace(root.getTotalSpace())
                            .usableSpace(root.getUsableSpace())
                            .build()
            );
        }
    }

    private void sendMessage(String topic, Object value) {
        var mqttChannel = ModuleUtil.getInputChannelName(mqttModule, ACTION_SEND);
        mqttMessagingTemplate.send(new MqttPayload(topic, value));
    }
}
