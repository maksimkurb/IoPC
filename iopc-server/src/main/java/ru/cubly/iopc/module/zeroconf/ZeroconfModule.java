package ru.cubly.iopc.module.zeroconf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.system.SystemProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.cubly.iopc.AbstractModule;
import ru.cubly.iopc.util.PlatformType;

import javax.annotation.PreDestroy;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.impl.DNSRecord;
import javax.jmdns.impl.ServiceInfoImpl;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ZeroconfModule extends AbstractModule {
    private List<JmDNS> mDNSResponders;

    @Value("${server.port}")
    private int port;

    @Value("${app.build-info.version}")
    private String appVersion;

    protected ZeroconfModule() {
        super("zeroconf", Arrays.asList(PlatformType.Windows, PlatformType.Linux, PlatformType.MacOS));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void publishService() {
        log.info("Starting mDNS discovery...");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("version", appVersion);
        parameters.put("java_version", SystemProperties.get("java.version"));

        try {
            mDNSResponders = NetworkInterface.networkInterfaces()
                    .filter(this::filterNetworkInterface)
                    .flatMap(NetworkInterface::inetAddresses)
                    .parallel()
                    .map(address -> publishService(address, parameters))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            publishService(new InetSocketAddress(0).getAddress(), parameters);

            log.info("Service announced");
        } catch (IOException e) {
            log.error("Failed to announce service", e);
        }
    }

    private boolean filterNetworkInterface(NetworkInterface interface_) {
        try {
            return interface_.isUp() &&
                    interface_.supportsMulticast() &&
                    !interface_.isPointToPoint() &&
                    !interface_.isLoopback();
        } catch (SocketException e) {
            return false;
        }
    }

    private JmDNS publishService(InetAddress address, Map<String, String> parameters) {
        if (address instanceof Inet6Address) return null;

        try {
            JmDNS jmDNS = JmDNS.create(address, address.getHostName());
            ServiceInfo serviceInfo = ServiceInfo.create("_iopc-server._tcp.local.", InetAddress.getLocalHost().getHostName(), port, 0, 0, parameters);

            jmDNS.registerService(serviceInfo);
            return jmDNS;
        } catch (IOException e) {
            log.error("Failed to start mDNS responder on inetAddress={}", address.getAddress(), e);
            return null;
        }
    }

    @PreDestroy
    public void unPublishService() {
        mDNSResponders.forEach(JmDNS::unregisterAllServices);
    }

}
