package org.jboss.fuse.security.oauth.junit;

import io.apiman.test.common.resttest.IGatewayTestServer;
import io.apiman.test.common.resttest.IGatewayTestServerFactory;

/**
 * Factory used to create the servlet version of the gateway for testing purposes.
 *
 * @author eric.wittmann@redhat.com
 */
public class ServletGatewayTestServerFactory implements IGatewayTestServerFactory {

    /**
     * Constructor.
     */
    public ServletGatewayTestServerFactory() {
    }

    /**
     * @see io.apiman.test.common.resttest.IGatewayTestServerFactory#createGatewayTestServer()
     */
    @Override
    public IGatewayTestServer createGatewayTestServer() {
        return new ServletGatewayTestServer();
    }

}
