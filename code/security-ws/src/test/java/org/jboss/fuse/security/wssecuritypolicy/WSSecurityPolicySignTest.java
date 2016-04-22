package org.jboss.fuse.security.wssecuritypolicy;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.jboss.fuse.security.SecurityTestUtil;
import org.jboss.fuse.security.Server;
import org.jboss.helloworld.Greeter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.URL;

public class WSSecurityPolicySignTest extends AbstractBusClientServerTestBase {

    private static final String PORT = allocatePort(Server.class);
    private static final String NAMESPACE = "http://jboss.org/HelloWorld";
    private static final QName SERVICE_QNAME = new QName(NAMESPACE, "GreeterService");

    @BeforeClass
    public static void startServers() throws Exception {
    }

    @AfterClass
    public static void cleanup() throws Exception {
        SecurityTestUtil.cleanup();
        stopAllServers();
    }

    /**
     *
     * Sign SOAP Body & TimeStamp
     */
    @Test public void testSignature() throws Exception {
    }

    private void runandValidate(URL busFile, String portName, String assertString, String wsdlFile) throws IOException {
    }
}
