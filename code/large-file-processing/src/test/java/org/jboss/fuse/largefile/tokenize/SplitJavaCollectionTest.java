package org.jboss.fuse.largefile.tokenize;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SplitJavaCollectionTest extends CamelTestSupport {

    @EndpointInject(uri="mock:result")
    MockEndpoint result;

    @Test
    public void testArrayOfString() throws Exception {
    }

    @Test
    public void testArrayOfPrimitive() throws Exception {
    }

    @Test
    public void testCollectionAndHeaderExpression() throws Exception {
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
    }
}