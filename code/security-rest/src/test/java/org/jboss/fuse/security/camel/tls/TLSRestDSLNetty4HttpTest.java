package org.jboss.fuse.security.camel.tls;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.http.JAASSecurityAuthenticator;
import org.apache.camel.component.netty4.http.NettyHttpConfiguration;
import org.apache.camel.component.netty4.http.NettyHttpSecurityConfiguration;
import org.apache.camel.component.netty4.http.SecurityAuthenticator;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestPropertyDefinition;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.jboss.fuse.security.camel.common.BaseJettyTest;
import org.jboss.fuse.security.camel.common.BaseNetty4Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class TLSRestDSLNetty4HttpTest extends BaseNetty4Test {

    private static String HOST = "localhost";
    private static int PORT = getPort1();
    protected String pwd = "secUr1t8";

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("nettyConf", getNettyHttpConfiguration());
        jndi.bind("mySecurityConfig", getSecurityHttpConfiguration());
        return jndi;
    }

    @Before
    @Override
    public void setUp() throws Exception {
        URL jaasURL = this.getClass().getResource("/org/jboss/fuse/security/basic/myrealm-jaas.cfg");
        setSystemProp("java.security.auth.login.config", jaasURL.toExternalForm());

        URL trustStoreUrl = this.getClass().getResource("serverstore.jks");
        setSystemProp("javax.net.ssl.trustStore", trustStoreUrl.toURI().getPath());

        // setSystemProp("javax.net.debug","ssl,handshake,data");

        super.setUp();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        restoreSystemProperties();
    }


    public URL getKeyStore() {
        return this.getClass().getResource("serverstore.jks");
    }

    @Test @Ignore public void simpleCamelHttpsCall() {
        String result = template.requestBodyAndHeader("netty4-http://https://localhost:" + PORT + "/say/hello/Charles","",Exchange.HTTP_METHOD,"GET",String.class);
        assertEquals("\"Hello World Charles\"",result);
    }

    @Test public void shouldSayHelloTest() {
        String user = "Charles";
        String strURL = "https://" + HOST + ":" + PORT + "/say/hello/" + user;

        HttpResult result = callRestEndpoint("localhost", strURL, "donald", "duck", "MyRealm");
        assertEquals(200, result.getCode());
        assertEquals("We should get a Hello World", "Hello World " + user,
                result.getMessage().replaceAll("^\"|\"$", ""));
    }

    private HttpResult callRestEndpoint(String host, String url, String user, String password, String realm) {

        HttpResult response = new HttpResult();

        // Define the Get Method with the String of the url to access the HTTP Resource
        GetMethod get = new GetMethod(url);

        // Set Credentials
        Credentials creds = new UsernamePasswordCredentials(user, password);
        // Auth Scope
        AuthScope authScope = new AuthScope(host, PORT, realm);

        // Execute request
        try {
            // Get HTTP client
            HttpClient httpclient = new HttpClient();
            // Use preemptive to select BASIC Auth
            httpclient.getParams().setAuthenticationPreemptive(true);
            httpclient.getState().setCredentials(authScope, creds);
            response.setCode(httpclient.executeMethod(get));

            InputStream is = get.getResponseBodyAsStream();
            response.setMessage(inputStreamToString(is));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release current connection to the connection pool once you are done
            get.releaseConnection();
        }

        return response;
    }


}
