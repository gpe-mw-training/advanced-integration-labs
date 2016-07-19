package org.jboss.fuse.largefile.parallel;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jboss.fuse.largefile.Utility;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelAggregateXMLSplitTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    MockEndpoint result;

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/split");
        super.setUp();
    }

    @Test
    public void testAggregate() {
    }

    @Test
    public void testParallel() throws Exception {
    }

    @Test
    public void testParallelThreadPool() throws Exception {
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
    }

    private class MyAggregationStrategy implements AggregationStrategy {
    }
}
