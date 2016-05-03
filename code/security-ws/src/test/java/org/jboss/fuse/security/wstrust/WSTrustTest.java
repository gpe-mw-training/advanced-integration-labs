package org.jboss.fuse.security.wstrust;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.hello_world_soap_http.Greeter;
import org.jboss.fuse.security.SecurityTestUtil;
import org.jboss.fuse.security.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class WSTrustTest extends AbstractBusClientServerTestBase {

    private static final QName SERVICE_NAME = new QName("http://apache.org/hello_world_soap_http",
            "SOAPService");

    private static final QName PORT_NAME = new QName("http://apache.org/hello_world_soap_http", "SoapPort");

    @BeforeClass
    public static void startServers() throws Exception {
    }

    @AfterClass public static void cleanup() throws Exception {
        SecurityTestUtil.cleanup();
        stopAllServers();
    }

    @Test public void testGreetMeClientWithSTS() throws Exception {
    }
}

