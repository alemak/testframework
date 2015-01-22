package com.netaporter.test.utils.network;

import java.lang.RuntimeException;import java.net.*;
import java.net.Inet4Address;import java.net.Inet6Address;import java.net.InetAddress;import java.net.NetworkInterface;import java.net.SocketException;import java.util.Enumeration;

public class NetworkUtil {

    public static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) {
                            if (preferIPv6) {
                                continue;
                            }
                            return inetAddress;
                        }
                        if (inetAddress instanceof Inet6Address) {
                            if (preferIpv4) {
                                continue;
                            }
                            return inetAddress;
                        }
                    }
                }
            }
        } catch (SocketException exception) {
            throw new RuntimeException("Could not ascertain the IP Address of the local machine.", exception);
        }

        throw new RuntimeException("Could not ascertain the IP Address of the local machine.");
    }
}
