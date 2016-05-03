package org.jboss.fuse.security.camel.tls;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpOperationFailedException;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.jboss.fuse.security.camel.common.BaseJettyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SslRestCamelDSLJettyTest extends BaseJettyTest {

    private static String HOST = "localhost";
    private static int PORT = getPort1();
    protected String pwd = "secUr1t8";

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("myAuthHandler", getSecurityHandler());
        jndi.bind("scp", getSSLContextParameters());
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

    @Test public void allowForUserDonaldAndRoleUser() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(Exchange.HTTP_METHOD,"GET");
        InputStream result = (InputStream) template.sendBodyAndHeaders("https4:localhost:" + PORT + "/say/hello/charles?sslContextParametersRef=#scp&authUsername=donald&authPassword=duck&authenticationPreemptive=true&authHost=localhost",ExchangePattern.InOut,"",headers);
        assertEquals("\"<b>Hello World</b>\"",inputStreamToString(result));
    }

    @Test public void sayByeNotAllowedForUserRoleTest() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(Exchange.HTTP_METHOD,"GET");
        try {
            InputStream result = (InputStream) template.sendBodyAndHeaders("https4:localhost:" + PORT + "/say/bye/charles?sslContextParametersRef=#scp&authUsername=donald&authPassword=duck&authenticationPreemptive=true&authHost=localhost",ExchangePattern.InOut,"",headers);
            fail();
        } catch(Exception e) {
            HttpOperationFailedException httpError = (HttpOperationFailedException) ((CamelExecutionException)e).getCause();
            assertEquals(403,httpError.getStatusCode());
            assertEquals("HTTP operation failed invoking https://localhost:23000/say/bye/charles with statusCode: 403",httpError.getMessage());
        }
    }


    private SSLContextParameters getSSLContextParameters() {
        // TLS
        KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource("org/jboss/fuse/security/camel/tls/serverstore.jks");
        ksp.setPassword(pwd);

        KeyManagersParameters kmp = new KeyManagersParameters();
        kmp.setKeyStore(ksp);
        kmp.setKeyPassword(pwd);

        SSLContextParameters scp = new SSLContextParameters();
        scp.setKeyManagers(kmp);
        return scp;
    }

    private SecurityHandler getSecurityHandler() throws IOException {

        /* A security handler is a jetty handler that secures content behind a
         *  particular portion of a url space. The ConstraintSecurityHandler is a
         *  more specialized handler that allows matching of urls to different
         *  constraints. The server sets this as the first handler in the chain,
         *  effectively applying these constraints to all subsequent handlers in
         *  the chain.
         *  The BasicAuthenticator instance is the object that actually checks the credentials
         */
        ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
        sh.setAuthenticator(new BasicAuthenticator());
        sh.setConstraintMappings(getConstraintMappings());

        /*
         * The DefaultIdentityService service handles only role reference maps passed in an
         * associated org.eclipse.jetty.server.UserIdentity.Scope.  If there are roles
         * refs present, then associate will wrap the UserIdentity with one that uses the role references in the
         * org.eclipse.jetty.server.UserIdentity#isUserInRole(String, org.eclipse.jetty.server.UserIdentity.Scope)}
         * implementation.
         *
        */
        DefaultIdentityService dis = new DefaultIdentityService();

        // Service which create a UserRealm suitable for use with JAAS
        JAASLoginService loginService = new JAASLoginService();
        loginService.setName("myrealm");
        loginService.setLoginModuleName("propsFileModule");
        loginService.setIdentityService(dis);

        sh.setLoginService(loginService);
        sh.setConstraintMappings(getConstraintMappings());

        return sh;
    }

    private List<ConstraintMapping> getConstraintMappings() {

        // Access allowed for roles User, Admin
        Constraint constraint0 = new Constraint(Constraint.__BASIC_AUTH, "user");
        constraint0.setAuthenticate(true);
        constraint0.setName("allowedForAll");
        constraint0.setRoles(new String[] { "user", "admin" });
        ConstraintMapping mapping0 = new ConstraintMapping();
        mapping0.setPathSpec("/say/hello/*");
        mapping0.setMethod("GET");
        mapping0.setConstraint(constraint0);

        // Access alowed only for Admin role
        Constraint constraint1 = new Constraint();
        constraint1.setAuthenticate(true);
        constraint1.setName("allowedForRoleAdmin");
        constraint1.setRoles(new String[]{ "admin" });
        ConstraintMapping mapping1 = new ConstraintMapping();
        mapping1.setPathSpec("/say/bye/*");
        mapping1.setMethod("GET");
        mapping1.setConstraint(constraint1);

        return Arrays.asList(mapping0, mapping1);
    }

}
