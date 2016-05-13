package org.jboss.fuse.security.camel.tls;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.http.*;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.*;
import org.jboss.fuse.security.camel.common.BaseNetty4Test;
import org.junit.Test;

import java.net.URL;

public class TLSRestDSLNetty4HttpTest extends BaseNetty4Test {

    private static String SCHEME_HTTPS = "https";
    private static int PORT = getPort1();

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();

        return jndi;

    }

    @Override
    public void setUp() throws Exception {
        // Realm included within the common file myrealm-jaas.cfg to avoid that the test fails when done with 'mvn clean test'
        // The object javax.security.auth.login.Configuration is instaiated one time / maven surefire session
        // and the object is not recreated with the System Prop
        URL jaasURL =  this.getClass().getResource("/org/jboss/fuse/security/basic/myrealm-jaas.cfg");
        setSystemProp("java.security.auth.login.config", jaasURL.toExternalForm());

        //setSystemProp("javax.net.debug","all");
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        restoreSystemProperties();
        super.tearDown();
    }

    @Test
    public void testFailAuth() {
    }

    @Test
    public void testBasicAuth()  {
    }

    @Test
    public void testBasicAuthSecConstraintWithoutAdminRole() {
    }




}
