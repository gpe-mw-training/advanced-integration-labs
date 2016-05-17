package org.jboss.fuse.security.cxf.mutualtls;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.validation.ValidationExceptionMapper;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.jboss.fuse.security.cxf.common.BaseCXF;
import org.jboss.fuse.security.cxf.service.CustomerService;
import org.jboss.fuse.security.cxf.service.CustomerServiceImpl;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class MutualTLSCxfRSTest extends BaseCXF {

    private static final Logger log = LoggerFactory.getLogger(MutualTLSCxfRSTest.class);
    public static final String PORT = allocatePort(MutualTLSCxfRSTest.class);
    private static JAXRSServerFactoryBean sf;

    @Ignore
    public static class Server extends AbstractBusTestServerBase {

        static {
        }

        protected void run() {
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
        System.setProperty("javax.net.debug","all");
        assertTrue("server did not launch correctly", launchServer(Server.class, true));
        createStaticBus();
    }

    @AfterClass
    public static void shutdown() {
        System.clearProperty("javax.net.debug");
        sf.getBus().shutdown(true);
    }

    @Test
    public void testMutualTLS() {
    }

    protected HttpResult callRestEndpoint(String host, String url) {

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

            Protocol.registerProtocol("https", new Protocol("https", new AuthSSLProtocolSocketFactory(keystoreUrl,keystorePwd,trustStoreUrl,trustStorePwd), port));

            // Get HTTP client
            HttpClient httpclient = new HttpClient();
            httpclient.getHostConfiguration().setHost(host);
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
