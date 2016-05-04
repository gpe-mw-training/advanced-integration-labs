package org.jboss.fuse.security.cxf.mutualtls;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.jboss.fuse.security.cxf.common.BaseCXF;
import org.jboss.fuse.security.cxf.service.CustomerServiceImpl;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.Scanner;

public class MutualTLSCxfRSTest extends BaseCXF {

    private static final Logger log = LoggerFactory.getLogger(MutualTLSCxfRSTest.class);
    public static final String PORT = allocatePort(MutualTLSCxfRSTest.class);
    private static JAXRSServerFactoryBean sf;

    @Ignore public static class Server extends AbstractBusTestServerBase {

        static {
            SpringBusFactory factory = new SpringBusFactory();
            Bus bus = factory.createBus("org/jboss/fuse/security/cxf/mutualtls/ServerConfig.xml");
            BusFactory.setDefaultBus(bus);
        }

        protected void run() {
            sf = new JAXRSServerFactoryBean();

            // Configure the Interceptor responsible to scan the Classes, Interface in order to detect @RolesAllowed Annotation
            // and creating a RolesMap
            SecureAnnotationsInterceptor sai = new SecureAnnotationsInterceptor();
            sai.setSecuredObject(new CustomerServiceImpl());
            sf.getInInterceptors().add(sai);

            sf.setResourceClasses(CustomerServiceImpl.class);
            sf.setProvider(new ValidationExceptionMapper());
            sf.setResourceProvider(CustomerServiceImpl.class,
                    new SingletonResourceProvider(new CustomerServiceImpl()));

            sf.setAddress("https://localhost:" + PORT + "/");

            sf.create();
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

    @BeforeClass public static void startServers() throws Exception {
        assertTrue("server did not launch correctly", launchServer(Server.class, true));
        createStaticBus();
    }

    @AfterClass public static void shutdown() {
        sf.getBus().shutdown(true);
    }

    @Test public void allowForDonalUserCorrectRoleTest() {

        String CustomerResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Customer><id>123</id><name>John</name></Customer>";
        String BASE_SERVICE_URL = "https://localhost:" + PORT + "/customerservice/customers/123";

        HttpResult res = callRestEndpoint("localhost", BASE_SERVICE_URL, "donald", "duck", "myrealm");

        Assert.assertEquals("Response status is 200", Response.Status.OK.getStatusCode(), res.getCode());
        Assert.assertEquals(CustomerResponse, res.getMessage());
    }

    protected HttpResult callRestEndpoint(String host, String url, String user, String password,
            String realm) {
        return callRestEndpoint(false, host, url);
    }

    protected HttpResult callRestEndpoint(boolean isHttps, String host, String url) {

        // Define the Get Method with the String of the url to access the HTTP Resource
        GetMethod get = new GetMethod(url);
        HttpResult response = new HttpResult();

        // Execute request
        try {

            int port = Integer.parseInt(PORT);

            URL keystoreUrl = this.getClass().getResource("clientKeystore.jks");
            String keystorePwd = "cspass";
            URL trustStoreUrl = this.getClass().getResource("clientKeystore.jks");
            String trustStorePwd = "cspass";

            Protocol.registerProtocol("https", new Protocol("https", new AuthSSLProtocolSocketFactory(keystoreUrl,keystorePwd,trustStoreUrl,trustStorePwd), 443));

            // Get HTTP client
            HttpClient httpclient = new HttpClient();
            get.setRequestHeader("Accept", "text/xml");
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
