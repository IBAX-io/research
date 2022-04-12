package io.ibax.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

public class CommonUtil {
    public static Long getNow() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        InetAddress inetAddress = getLocalHostLANAddress();
        System.out.println(inetAddress.getHostName());
    }

    public static String getLocalIp() {
        InetAddress inetAddress = getLocalHostLANAddress();
        if (inetAddress != null) {
            return inetAddress.getHostAddress();
        }
        return null;
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    private static InetAddress getLocalHostLANAddress() {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            return InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
