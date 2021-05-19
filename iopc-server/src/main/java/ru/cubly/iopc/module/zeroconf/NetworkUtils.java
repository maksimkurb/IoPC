package ru.cubly.iopc.module.zeroconf;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

@Service
@Log4j2
public class NetworkUtils {

    private static final byte[] ROUTABLE_HOST_ADDRESS_IPv4 = {1, 1, 1, 1};

    public InetAddress getAddress(final String... addresses) throws IOException {
        for (final String address : addresses) {
            try {
                return InetAddress.getByName(address);
            } catch (final UnknownHostException e) {
                log.trace("Not a known name: {}", address);
            }
        }
        log.debug("No interface found by name search");

        // scan all interface for a working internet connection
        return getMainAddress();
    }

    public InetAddress getMainAddress() throws IOException {
        // iterate over the network interfaces known to java
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface interface_ : Collections.list(interfaces)) {
            if ( //
                    !interface_.isUp() ||               // a down interface is not useful for us, isn't it
                            !interface_.supportsMulticast() ||  // MC is required for mDNS
                            interface_.isPointToPoint() ||      // we care only about regular interfaces
                            interface_.isLoopback()             // don't care about loopback addresses
            ) {
                continue;
            }

            // iterate over the addresses associated with the interface
            final Enumeration<InetAddress> addresses = interface_.getInetAddresses();
            for (final InetAddress address : Collections.list(addresses)) {
                if (address.isLinkLocalAddress()) {
                    continue; // need a real address
                }
                try (final DatagramSocket datagramSocket = new DatagramSocket()) {
                    // try to connect to *somewhere*
                    datagramSocket.connect(new InetSocketAddress("8.8.8.8", 53));
                } catch (IOException ex) {
                    log.debug("IF: {}, failed: {}", interface_, ex);
                    continue;
                }

                log.debug("IF: {}, addr: {}", interface_, address);

                // stops at the first *working* solution
                return address;
            }
        }

        return InetAddress.getLocalHost();
    }

    public InetAddress getMainAddress(final InetAddress inetAddress) throws IOException {
        // iterate over the network interfaces known to java
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (final NetworkInterface interface_ : Collections.list(interfaces)) {
            if ( //
                    !interface_.isUp() ||               // a down interface is not useful for us, isn't it
                            !interface_.supportsMulticast() ||  // MC is required for mDNS
                            interface_.isPointToPoint() ||      // we care only about regular interfaces
                            interface_.isLoopback()             // don't care about loopback addresses
            ) {
                continue;
            }

            // iterate over the addresses associated with the interface
            final Enumeration<InetAddress> addresses = interface_.getInetAddresses();
            for (final InetAddress address : Collections.list(addresses)) {
                try (final DatagramSocket datagramSocket = new DatagramSocket()) {
                    // the port doesn't matter we just need a routable address
                    datagramSocket.connect(inetAddress, 53);
                } catch (final IOException e) {
                    log.debug("IF: {}, failed: {}", interface_, e);
                    continue;
                }

                log.debug("IF: {}, addr: {}", interface_, address);

                // stops at the first *working* solution
                return address;
            }
        }

        throw new IOException("No valid interface/address found!");
    }

    public InetAddress getMainAddressIPv4() throws IOException {
        final InetAddress inetAddressV4 = InetAddress.getByAddress(ROUTABLE_HOST_ADDRESS_IPv4);
        return getMainAddress(inetAddressV4);
    }

}