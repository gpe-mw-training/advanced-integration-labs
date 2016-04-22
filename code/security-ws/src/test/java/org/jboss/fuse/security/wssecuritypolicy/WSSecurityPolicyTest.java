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

public class WSSecurityPolicyTest extends AbstractBusClientServerTestBase {

    private static final String PORT = allocatePort(Server.class);
    private static final String NAMESPACE = "http://jboss.org/HelloWorld";
    private static final QName SERVICE_QNAME = new QName(NAMESPACE, "GreeterService");

    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("Server failed to launch",
                // run the server in the same process
                // set this to false to fork
                launchServer(Server.class, null, new String[] { "/org/jboss/fuse/security/wssecuritypolicy/server.xml" }, true));
    }

    @AfterClass
    public static void cleanup() throws Exception {
        SecurityTestUtil.cleanup();
        stopAllServers();
    }


    /**
     * Define a WS Security Policy to generate the SOAP Header including a wsse section with a username and timestamp to authenticate the JAXWS Client
     */
    @Test public void testUsernameToken() throws Exception {
    }

    /**
     * Define a WS Security Policy to generate the SOAP Header including a wsse section with a username, wrong password and timestamp to authenticate the JAXWS Client
     */
    @Test public void testUsernameTokenWrongPassword() throws Exception {
    }

    private void runandValidate(URL busFile, String portName, String assertString, String wsdlFile) throws IOException {
    }
}
