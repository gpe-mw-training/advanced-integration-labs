package org.jboss.fuse.security.apiman.server;


/**
 * Factory used to create the servlet version of the gateway for testing purposes.
 *
 */
public class ServletGatewayTestServerFactory implements IGatewayTestServerFactory {

    /**
     * Constructor.
     */
    public ServletGatewayTestServerFactory() {
    }

    @Override
    public IGatewayTestServer createGatewayTestServer() {
        return new ServletGatewayTestServer();
    }

}
