package org.jboss.fuse.largefile.performance;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SpeedWriteProcessTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CamelTestSupport.class);

    private int files = 10;
    private int rows = 50000;
    private int total = (files * rows) + files;

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/data/out");
        super.setUp();
    }

    @Test
    public void testNoStreamNoAggregate() throws Exception {
    }

    @Test
    public void testStreamWithoutAggregation() throws Exception {
    }

    @Test
    public void testStreamWithAggregation() throws Exception {
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
    }

    private class MyAggregationStrategy implements AggregationStrategy {
    }

}

