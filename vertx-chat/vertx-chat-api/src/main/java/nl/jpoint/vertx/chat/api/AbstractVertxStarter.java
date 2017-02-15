package nl.jpoint.vertx.chat.api;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.net.*;
import java.util.*;

import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.Arrays.asList;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Abstract starter class that takes care of clustering
 */
public class AbstractVertxStarter {

    private static Logger LOG = LoggerFactory.getLogger(AbstractVertxStarter.class);
    public static final String IP_PREFIX = "192.168.1";

    /**
     * Deploys the given verticle in the clustered environment.
     * @param verticle The {@code Verticle} to deploy.
     */
    protected static void deployVerticle(Verticle verticle) throws SocketException {
        deployVerticles(verticle);
    }

    /**
     * Deploys the given verticles in the clustered environment.
     * @param verticles The {@code Verticle}s to deploy.
     */
    protected static void deployVerticles(Verticle... verticles) throws SocketException {
        Vertx.clusteredVertx(createVertxOptions(),
                resultHandler -> asList(verticles).forEach(
                        verticle -> resultHandler.result().deployVerticle(verticle)
                )
        );
    }

    /**
     * Creates the {@code VertxOptions} to setup the clustering.
     * @return the {@code VertxOptions}
     */
    protected static VertxOptions createVertxOptions() throws SocketException {
        String clusterAddress = getClusterIpAddress();
        return new VertxOptions().setClustered(true).setClusterHost(clusterAddress);
    }

    /**
     * Determine the local ip-address to cluster on.
     * @return The ip address in the range specified by IP_PREFIX
     */
    private static String getClusterIpAddress() throws SocketException {
        Optional<String> ipAddress = getMyIpAddresses().stream()
                .filter(ip -> ip.startsWith(IP_PREFIX))
                .findFirst();
        if (!ipAddress.isPresent()) {
            throw new RuntimeException("IP address with prefix '" + IP_PREFIX + "' not found. " +
                    "Set constant " + AbstractVertxStarter.class.getName() + ".IP_PREFIX " +
                    "to the correct value for the current network.");
        }

        return ipAddress.get();
    }

    /**
     * Get all local ip addresses.
     * @return a list with the ip-addresses from all the network interfaces.
     */
    private static List<String> getMyIpAddresses() throws SocketException {
        try {
            return stream(spliteratorUnknownSize(fromEnumeration(getNetworkInterfaces()), 0), false)
                    .flatMap(iface -> iface.getInterfaceAddresses().stream())
                    .map(InterfaceAddress::getAddress)
                    .map(InetAddress::getHostAddress)
                    .collect(toList());
        } catch (SocketException e) {
            LOG.error("Error while determining IP addresses: ", e);
            throw e;
        }
    }

    /**
     * Utility method to make an Iterator from an Enumeration
     * Inspired by https://bugs.openjdk.java.net/browse/JDK-8072726
     * @param enumeration The enumeration to create an {@code Iterator for}.
     * @return an {@code Iterator}.
     */
    private static <T> Iterator<T> fromEnumeration(Enumeration<? extends T> enumeration) {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        };
    }
}
