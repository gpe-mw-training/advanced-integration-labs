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

        // Netty with HTTPS scheme, JAAS Auth & Security Path Constraint & Role
        jndi.bind("nettyServerConfiguration", getNettyHttpSslConfiguration());
        jndi.bind("nettyServerSecurityConfig", getJAASSecurityHttpConfiguration());

        // jndi.bind("nettyClientConf", getNettyHttpClientSslConfiguration()); // DOESN't WORK for the producer
        // Pass to the Netty Producer the SSL Context Parameters
        jndi.bind("sslClientParameters", getClientSSLContextParameters());
        return jndi;
    }

    @Override
    public void setUp() throws Exception {
        // Realm included within the common file myrealm-jaas.cfg to avoid that the test fails when done with 'mvn clean test'
        // The object javax.security.auth.login.Configuration is instaiated one time / maven surefire session
        // and the object is not recreated with the System Prop
        URL jaasURL =  this.getClass().getResource("/org/jboss/fuse/security/basic/myrealm-jaas.cfg");
        setSystemProp("java.security.auth.login.config", jaasURL.toExternalForm());

        URL trustStoreUrl = this.getClass().getResource("clientKeystore.jks");
        setSystemProp("javax.net.ssl.trustStore", trustStoreUrl.toURI().getPath());

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
        try {
            template.requestBody("netty4-http://https://localhost:" + PORT + "/say/hello/noauthheader","", String.class);
            fail("Should send back 401");
        } catch (CamelExecutionException e) {
            NettyHttpOperationFailedException cause = assertIsInstanceOf(NettyHttpOperationFailedException.class, e.getCause());
            assertEquals(401, cause.getStatusCode());
        }
    }

    @Test
    public void testBasicAuth()  {

        // username:password is mickey:mouse
        String auth = "Basic bWlja2V5Om1vdXNl";
        String result = template.requestBodyAndHeader("netty4-http://https://localhost:" + PORT + "/say/hello/Mickey?ssl=true&sslContextParameters=#sslClientParameters", "", "Authorization", auth, String.class);
        assertEquals("\"Hello World Mickey\"", result);
    }

    @Test
    public void testBasicAuthSecConstraintWithoutAdminRole() {
        // username:password is donald:duck
        String auth = "Basic ZG9uYWxkOmR1Y2s=";

        // User without Admin Role
        try {
            String result = template.requestBodyAndHeader("netty4-http://https://localhost:" + PORT + "/say/hello/Donald?ssl=true&sslContextParameters=#sslClientParameters", "", "Authorization", auth, String.class);
            fail("Should send back 401");
        } catch (CamelExecutionException e) {
            //NettyHttpOperationFailedException cause = assertIsInstanceOf(NettyHttpOperationFailedException.class, e.getCause());
            //assertEquals(401, cause.getStatusCode());
            e.printStackTrace();
        }
    }

    @Test
    public void testBasicAuthAndSecConstraint() {
        // username:password is mickey:mouse
        String auth = "Basic bWlja2V5Om1vdXNl";

        // User with Role Admin
        String result = template.requestBodyAndHeader("netty4-http://https://localhost:" + PORT + "/say/hello/Mickey?ssl=true&sslContextParameters=#sslClientParameters", "", "Authorization", auth, String.class);
        assertEquals("\"Hello World Mickey\"", result);
    }


    /*
     * NettyHttpConfiguration with SSL parameters
     */
    private NettyHttpConfiguration getNettyHttpClientSslConfiguration() {
        NettyHttpConfiguration conf = new NettyHttpConfiguration();
        conf.setSsl(true);
        //conf.setKeyStoreResource("org/jboss/fuse/security/camel/tls/clientKeystore.jks");
        //conf.setTrustStoreResource("org/jboss/fuse/security/camel/tls/clientKeystore.jks");
        conf.setSslContextParameters(getClientSSLContextParameters());
        return conf;
    }

    private SSLContextParameters getClientSSLContextParameters() {
        // TLS
        KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource("org/jboss/fuse/security/camel/tls/clientKeystore.jks");
        ksp.setPassword("cspass");

        TrustManagersParameters tmp = new TrustManagersParameters();
        tmp.setKeyStore(ksp);

        //KeyManagersParameters kmp = new KeyManagersParameters();
        //kmp.setKeyStore(ksp);
        //kmp.setKeyPassword("ckpass");

        SSLContextParameters scp = new SSLContextParameters();
        //scp.setKeyManagers(kmp);
        scp.setTrustManagers(tmp);
        scp.setSecureSocketProtocol("SSLv3");
        return scp;
    }

}
