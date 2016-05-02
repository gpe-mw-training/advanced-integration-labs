package org.jboss.fuse.security.camel.common;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseJettyTest extends CamelTestSupport {

    private static volatile int port1;
    private static volatile int port2;

    private static final String NULL_VALUE_MARKER = CamelTestSupport.class.getCanonicalName();

    private final AtomicInteger counter = new AtomicInteger(1);
    protected Properties originalValues = new Properties();

    @BeforeClass
    public static void initPort() throws Exception {
        // start from somewhere in the 23xxx range
        port1 = AvailablePortFinder.getNextAvailable(23000);
        // start from somewhere in the 23xxx range
        port2 = AvailablePortFinder.getNextAvailable(24000);
    }

    protected void setSystemProp(String key, String value) {
        String originalValue = System.setProperty(key, value);
        originalValues.put(key, originalValue != null ? originalValue : NULL_VALUE_MARKER);
    }

    protected void restoreSystemProperties() {
        for (Object key : originalValues.keySet()) {
            Object value = originalValues.get(key);
            if (NULL_VALUE_MARKER.equals(value)) {
                System.getProperties().remove(key);
            } else {
                System.setProperty((String)key, (String)value);
            }
        }
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

    protected String inputStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public class HttpResult {
        int code;
        String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

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
