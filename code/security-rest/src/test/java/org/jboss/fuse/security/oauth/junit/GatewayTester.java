package org.jboss.fuse.security.oauth.junit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import io.apiman.test.common.resttest.IGatewayTestServer;
import io.apiman.test.common.resttest.IGatewayTestServerFactory;
import io.apiman.test.common.resttest.RestTest;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static org.jboss.fuse.security.oauth.junit.GatewayTestUtil.*;

/**
 * A junit test runner that fires up an API Gateway and makes it ready for use
 * in the tests.
 */
@SuppressWarnings("nls")
public class GatewayTester extends BlockJUnit4ClassRunner {

    private static IGatewayTestServer gatewayServer;

    static {
        createAndConfigureGateway();
    }

    private OkHttpClient client = new OkHttpClient();

    {
        client.setFollowRedirects(false);
        client.setFollowSslRedirects(false);
    }

    private static String endpoint = "api";

    /**
     * Constructor.
     *
     * @param testClass the test class
     * @throws InitializationError the initialziation error
     */
    public GatewayTester(Class<?> testClass) throws InitializationError {
        super(testClass);
        configureSystemProperties(getTestClass());
    }

    /**
     * Creates a gateway from a gateway test config file.
     */
    protected static void createAndConfigureGateway() {
        String testConfig = System.getProperty("apiman.gateway-test.config", null);
        if (testConfig == null) {
            testConfig = "default";
        }
        URL configUrl = GatewayTester.class.getClassLoader()
                .getResource("test-configs/" + testConfig + ".json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode config = mapper.readTree(configUrl);
            String factoryFQN = config.get("factory").asText();
            IGatewayTestServerFactory factory = (IGatewayTestServerFactory) Class.forName(factoryFQN)
                    .newInstance();
            gatewayServer = factory.createGatewayTestServer();
            gatewayServer.configure(config);
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @throws Exception
     */
    protected static void startServer() {
        try {
            gatewayServer.start();
            System.setProperty("apiman-gateway-test.endpoints.echo", gatewayServer.getEchoTestEndpoint());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @throws Exception
     */
    protected static void stopServer() {
        try {
            gatewayServer.stop();
            resetSystemProperties();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void runChild(final FrameworkMethod method, RunNotifier notifier) {
        setupGateway(method, resolveEndpoint(endpoint));
        super.runChild(method, notifier);
    }

    /**
     * @see org.junit.runners.ParentRunner#run(org.junit.runner.notification.RunNotifier)
     */
    @Override public void run(final RunNotifier notifier) {
        // For every run, we need to set up an instance of the apiman engine.
        startServer();
        super.run(notifier);
        stopServer();

    }

    /**
     * Configure the Gateway
     *
     * @param baseApiUrl
     * @throws Error
     */
    public void setupGateway(FrameworkMethod method, String baseApiUrl) throws Error {

        try {

            // Get the configuration JSON to use
            Configuration config = method.getMethod().getAnnotation(Configuration.class);
            if (config == null) {
                config = getTestClass().getJavaClass().getAnnotation(Configuration.class);
            }
            if (config == null) {
                throw new Exception("Missing test annotation @Configuration.");
            }

            RestTest restTest;

            for (String cfg : config.cpConfigFiles()) {
                restTest = GatewayTestUtil.loadRestTest(cfg, getTestClass().getClass().getClassLoader());

                String requestPath = GatewayTestUtil.doPropertyReplacement(restTest.getRequestPath());
                URI uri = getUri(baseApiUrl, requestPath);
                String rawType = restTest.getRequestHeaders().get("Content-Type") != null ?
                        restTest.getRequestHeaders().get("Content-Type") :
                        "text/plain; charset=UTF-8";
                MediaType mediaType = MediaType.parse(rawType);

                log("Sending HTTP request to: " + uri);

                RequestBody body = null;
                if (restTest.getRequestPayload() != null && !restTest.getRequestPayload().isEmpty()) {
                    body = RequestBody.create(mediaType, restTest.getRequestPayload());
                }

                Request.Builder requestBuilder = new Request.Builder().url(uri.toString())
                        .method(restTest.getRequestMethod(), body);

                Map<String, String> requestHeaders = restTest.getRequestHeaders();
                for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                    String value = GatewayTestUtil.doPropertyReplacement(entry.getValue());
                    // Handle system properties that may be configured in the rest-test itself
                    if (entry.getKey().startsWith("X-RestTest-System-Property")) {
                        String[] split = value.split("=");
                        System.setProperty(split[0], split[1]);
                        continue;
                    }
                    requestBuilder.addHeader(entry.getKey(), value);
                }

                // Set up basic auth
                String authorization = createBasicAuthorization(restTest.getUsername(), restTest.getPassword());
                if (authorization != null) {
                    requestBuilder.addHeader("Authorization", authorization);
                }

                Response response = client.newCall(requestBuilder.build()).execute();
                GatewayTestUtil.assertResponse(restTest, response);
            }

        } catch (Error e) {
            logPlain("[ERROR] " + e.getMessage());
            throw e;
        } catch (ProtocolException e) {
            logPlain("[HTTP PROTOCOL EXCEPTION]" + e.getMessage());
        } catch (Exception e) {
            logPlain("[EXCEPTION] " + e.getMessage());
            throw new Error(e);
        }
    }

    /**
     * Resolves the logical endpoint into a real endpoint provided by the {@link IGatewayTestServer}.
     *
     * @param endpoint
     */
    protected String resolveEndpoint(String endpoint) {
        if ("api".equals(endpoint)) {
            return gatewayServer.getApiEndpoint();
        } else if ("gateway".equals(endpoint)) {
            return gatewayServer.getGatewayEndpoint();
        } else {
            return GatewayTestUtil.doPropertyReplacement(endpoint);
        }
    }

}
