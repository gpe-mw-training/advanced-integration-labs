package org.jboss.fuse.security.camel.tls;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.http.*;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.jboss.fuse.security.camel.common.BaseNetty4Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

public class TLSRestDSLNetty4HttpTest extends BaseNetty4Test {

    private static String HOST = "localhost";
    private static String SCHEME_HTTP = "http";
    private static String SCHEME_HTTPS = "https";
    private static int PORT = getPort1();
    protected String pwd = "secUr1t8";

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();

        // Netty with HTTPS scheme, JAAS Auth & Security Path Constraint & Role
        jndi.bind("nettyConf", getNettyHttpSslConfiguration());
        jndi.bind("mySecurityConfig", getJAASSecurityHttpConfiguration());
        return jndi;
    }

    @Before
    @Override public void setUp() throws Exception {
        URL jaasURL = this.getClass().getResource("myjaas.config");
        setSystemProp("java.security.auth.login.config", jaasURL.toExternalForm());

        URL trustStoreUrl = this.getClass().getResource("serverstore.jks");
        setSystemProp("javax.net.ssl.trustStore", trustStoreUrl.toURI().getPath());

        //setSystemProp("javax.net.debug","ssl,handshake,data");
        super.setUp();
    }

    @After
    @Override public void tearDown() throws Exception {
        super.tearDown();
        restoreSystemProperties();
    }

    @Test public void testBasicAuth() {
        String result;

        try {
            template.requestBody("netty4-http://https://localhost:" + PORT + "/say/hello/noauthheader", "",
                    String.class);
            fail("Should send back 401");
        } catch (CamelExecutionException e) {
            NettyHttpOperationFailedException cause = assertIsInstanceOf(
                    NettyHttpOperationFailedException.class, e.getCause());
            assertEquals(401, cause.getStatusCode());
        }

        // username:password is mickey:mouse
        String auth = "Basic bWlja2V5Om1vdXNl";
        result = template.requestBodyAndHeader("netty4-http://https://localhost:" + PORT + "/say/hello/Donald", "", "Authorization", auth, String.class);
        assertEquals("\"Hello World Donald\"", result);
    }

    @Test public void testBasicAuthAndSecConstraint() {
        String result;
        // username:password is donald:duck
        String auth = "Basic ZG9uYWxkOmR1Y2s=";

        // User without Admin Role
        try {
            result = template.requestBodyAndHeader("netty4-http://https://localhost:" + PORT + "/say/hello/Donald", "", "Authorization", auth, String.class);
            fail("Should send back 401");
        } catch (CamelExecutionException e) {
            NettyHttpOperationFailedException cause = assertIsInstanceOf(NettyHttpOperationFailedException.class, e.getCause());
            assertEquals(401, cause.getStatusCode());
        }

        // username:password is mickey:mouse
        auth = "Basic bWlja2V5Om1vdXNl";

        // User with Role Admin
        result = template.requestBodyAndHeader("netty4-http://https://localhost:" + PORT + "/say/hello/Mickey", "", "Authorization", auth, String.class);
        assertEquals("\"Hello World Mickey\"", result);
    }


}
