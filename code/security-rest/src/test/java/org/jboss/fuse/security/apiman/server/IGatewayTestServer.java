package org.jboss.fuse.security.apiman.server;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Any gateway under test would need to implement this interface, along
 * with standing up an actual gateway instance (with API).  The gateway
 * tests will then send http messages to the appropriate endpoints
 * in order to affect the test being run.
 */
public interface IGatewayTestServer {

    public void configure(JsonNode config);

    public String getApiEndpoint();

    public String getGatewayEndpoint();

    public String getTestEndpoint();

    public void start();

    public void stop();

}