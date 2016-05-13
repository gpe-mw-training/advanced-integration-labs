package org.jboss.fuse.security.cxf.role;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.jboss.fuse.security.cxf.common.BaseCXF;
import org.jboss.fuse.security.cxf.service.CustomerServiceWithRole;
import org.jboss.fuse.security.cxf.service.CustomerServiceWithRoleImpl;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class BasicAuthCxfRSRoleTest extends BaseCXF {

    private static final Logger log = LoggerFactory.getLogger(BasicAuthCxfRSRoleTest.class);
    public static final String PORT = allocatePort(BasicAuthCxfRSRoleTest.class);
    private static JAXRSServerFactoryBean sf;

    @Ignore
    public static class Server extends AbstractBusTestServerBase {

        static {
        }

        protected void run() {
            sf = new JAXRSServerFactoryBean();
        }

        public static void main(String[] args) {
            try {
                Server s = new Server();
                s.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            } finally {
                System.out.println("done!");
            }
        }
    }

    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("server did not launch correctly", launchServer(Server.class, true));
        createStaticBus();
    }

    @AfterClass
    public static void shutdown() {
        sf.getBus().shutdown(true);
    }

    @Test
    public void allowForDonalUserCorrectRoleTest() {
        String CustomerResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Customer><id>123</id><name>John</name></Customer>";
        String BASE_SERVICE_URL = "http://localhost:" + PORT + "/customerservice/customers/123";

        HttpResult res = callRestEndpoint("localhost", BASE_SERVICE_URL, "donald", "duck", "myrealm");
    }

    @Test
    public void deniedForUmperioNotCorrectRole() {
        String BASE_SERVICE_URL = "http://localhost:" + PORT + "/customerservice/customers/123";

        HttpResult res = callRestEndpoint("localhost", BASE_SERVICE_URL, "umperio", "bogarto", "myrealm");
    }

    protected HttpResult callRestEndpoint(String host, String url, String user, String password, String realm) {

        HttpResult response = new HttpResult();

        // Define the Get Method with the String of the url to access the HTTP Resource
        GetMethod get = new GetMethod(url);
        get.setRequestHeader("Accept", "text/xml");

        // Set Credentials
        Credentials creds = new UsernamePasswordCredentials(user, password);
        // Auth Scope
        AuthScope authScope = new AuthScope(host, Integer.parseInt(PORT), realm);

        // Execute request
        try {
            // Get HTTP client
            HttpClient httpclient = new HttpClient();
            // Use preemptive to select BASIC Auth
            httpclient.getParams().setAuthenticationPreemptive(true);
            httpclient.getState().setCredentials(authScope, creds);
            response.setCode(httpclient.executeMethod(get));

            InputStream is = get.getResponseBodyAsStream();
            Scanner s = new Scanner(is).useDelimiter("\\A");
            response.setMessage(s.hasNext() ? s.next() : "");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release current connection to the connection pool once you are done
            get.releaseConnection();
        }

        return response;
    }

}
