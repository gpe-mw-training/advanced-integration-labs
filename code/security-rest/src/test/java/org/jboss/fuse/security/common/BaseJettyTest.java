package org.jboss.fuse.security.common;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseJettyTest extends CamelTestSupport{

    private static volatile int port1;
    private static volatile int port2;

    private final AtomicInteger counter = new AtomicInteger(1);

    @BeforeClass
    public static void initPort() throws Exception {
        // start from somewhere in the 23xxx range
        port1 = AvailablePortFinder.getNextAvailable(23000);
        // start from somewhere in the 23xxx range
        port2 = AvailablePortFinder.getNextAvailable(24000);
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.addComponent("properties", new PropertiesComponent("ref:prop"));
        return context;
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        Properties prop = new Properties();
        prop.setProperty("port1", "" + getPort1());
        prop.setProperty("port2", "" + getPort1());
        jndi.bind("prop", prop);
        return jndi;
    }

    protected int getNextPort() {
        return AvailablePortFinder.getNextAvailable(port1 + counter.getAndIncrement());
    }

    protected static int getPort1() {
        return port1;
    }
    protected static int getPort2() {
        return port2;
    }
}
