package org.jboss.fuse.security;

import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;

import java.util.concurrent.atomic.AtomicInteger;

public class BaseJettyTest extends CamelTestSupport{

    private static volatile int port;

    private static volatile int port2;

    private final AtomicInteger counter = new AtomicInteger(1);

    @BeforeClass
    public static void initPort() throws Exception {
        // start from somewhere in the 23xxx range
        port = AvailablePortFinder.getNextAvailable(23000);
        // find another ports for proxy route test
        port2 = AvailablePortFinder.getNextAvailable(24000);
    }

    protected static int getPort() {
        return port;
    }
}
