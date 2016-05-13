package org.jboss.fuse.security.camel.policy;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.http.NettyHttpOperationFailedException;
import org.apache.camel.model.rest.RestBindingMode;
import org.jboss.fuse.security.camel.common.BaseNetty4Test;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class SimpleAutPolicyTest extends BaseNetty4Test {

    private static int PORT = getPort1();

    @Test
    public void testBasicAuth() {
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
    }
}
