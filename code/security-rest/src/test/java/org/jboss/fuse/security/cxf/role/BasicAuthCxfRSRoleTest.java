package org.jboss.fuse.security.cxf.role;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.model.AbstractResourceInfo;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.jboss.fuse.security.cxf.common.BaseCXF;
import org.jboss.fuse.security.cxf.service.CustomerServiceImpl;
import org.junit.*;

import javax.ws.rs.core.Response;
import java.io.InputStream;

public class BasicAuthCxfRSRoleTest extends BaseCXF {

    public static final String PORT = allocatePort(BasicAuthCxfRSRoleTest.class);

    @Ignore public static class Server extends AbstractBusTestServerBase {
        protected void run() {
            JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

            sf.setResourceClasses(CustomerServiceImpl.class);
            // sf.setProvider(new ValidationExceptionMapper());
            sf.setResourceProvider(CustomerServiceImpl.class,
                    new SingletonResourceProvider(new CustomerServiceImpl()));

            sf.setAddress("http://localhost:" + PORT + "/");
            // sf.setInvoker(new JAXRSBeanValidationInvoker());

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

    @Test public void allowForUserTest() {
        // String keyStoreLoc = "src/main/config/clientKeystore.jks";

        // KeyStore keyStore = KeyStore.getInstance("JKS");
        // keyStore.load(new FileInputStream(keyStoreLoc), "cspass".toCharArray());

        /*
         * Send HTTP GET request to query customer info using portable HttpClient
         * object from Apache HttpComponents
         */

        String CustomerResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Customer><id>123</id><name>John</name></Customer>";
        GetMethod get = null;
        String BASE_SERVICE_URL = "http://localhost:" + PORT + "/customerservice/customers";

        try {
            // Define the Get Method with the String of the url to access the HTTP Resourc
            get = new GetMethod(BASE_SERVICE_URL + "/123");
            get.setRequestHeader("Accept", "text/xml");
            HttpClient httpclient = new HttpClient();
            int status = httpclient.executeMethod(get);

            InputStream is = get.getResponseBodyAsStream();
            String response = inputStreamToString(is);

            Assert.assertEquals("Response status is 200", Response.Status.OK.getStatusCode(), status);
            Assert.assertEquals(CustomerResponse, response);

/*
        *//*
         *  Send HTTP PUT request to update customer info, using CXF WebClient method
         *  Note: if need to use basic authentication, use the WebClient.create(baseAddress,
         *  username,password,configFile) variant, where configFile can be null if you're
         *  not using certificates.
         *//*
        System.out.println("\n\nSending HTTPS PUT to update customer name");
        WebClient wc = WebClient.create(BASE_SERVICE_URL, CLIENT_CONFIG_FILE);
        Customer customer = new Customer();
        customer.setId(123);
        customer.setName("Mary");
        Response resp = wc.put(customer);

        *//*
         *  Send HTTP POST request to add customer, using JAXRSClientProxy
         *  Note: if need to use basic authentication, use the JAXRSClientFactory.create(baseAddress,
         *  username,password,configFile) variant, where configFile can be null if you're
         *  not using certificates.
         *//*
        System.out.println("\n\nSending HTTPS POST request to add customer");
        CustomerService proxy = JAXRSClientFactory.create(BASE_SERVICE_URL, CustomerService.class,
              CLIENT_CONFIG_FILE);
        customer = new Customer();
        customer.setName("Jack");
        resp = wc.post(customer);
        */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release current connection to the connection pool once you are done
            get.releaseConnection();
        }
    }

}
