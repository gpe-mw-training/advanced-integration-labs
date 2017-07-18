package org.jboss.fuse.security.camel.basic.role;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestPropertyDefinition;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.jboss.fuse.security.camel.common.BaseJettyTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicAuthRESTCamelDSLJettyRoleConstraintTest extends BaseJettyTest {

    private static String HOST = "localhost";
    private static int PORT = getPort1();

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        return jndi;
    }

    /*
    @Before
    public void init() throws IOException {
    }
    */

    @Test
    public void shouldSayHelloTest() {
    }

    @Test
    public void sayByeNotAllowedForUserRoleTest() {
    }

    @Test
    public void sayByeAllowedForAdminRoleTest() {
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


    private SecurityHandler getSecurityHandler() throws IOException {
    }

    private List<ConstraintMapping> getConstraintMappings() {
    }

}
