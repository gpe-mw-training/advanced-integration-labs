package org.jboss.fuse.largefile.performance;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class StaxDomTest extends CamelTestSupport {

    private int files = 10;
    private int rows = 100000;
    private int total = (files * rows) + files;
    private Runtime runtime;

    private static final Logger LOG = LoggerFactory.getLogger(CamelTestSupport.class);
    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    @Override
    public void setUp() throws Exception {
        runtime = Runtime.getRuntime();
        runtime.gc();
        super.setUp();
    }

    @Test
    public void testWithXTokenizer() throws Exception {
    }

    @Test
    public void testWithXPath() throws Exception {
    }

    @Test @Ignore
    public void testDummy() throws Exception {
    }

    @Override
    protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {

            Namespaces ns = new Namespaces("acme", "http:acme.com");

            public void configure() {
                from("file:target/data?noop=true").id("xtokenize").noAutoStartup()
                    .split().xtokenize("//acme:record",ns).streaming()
                    .log(LoggingLevel.DEBUG,"Record : ${body}");

                // Use DOM and load all XML structure in memory
                from("file:target/data?noop=true").id("dom").noAutoStartup()
                    .convertBodyTo(String.class)
                    .split().xpath("//acme:record",ns)
                        .log(LoggingLevel.DEBUG,"Record : ${body}");

                // Use DOM and load all XML structure in memory
                from("direct:test")
                        .log(LoggingLevel.INFO,"Records : ${body}")
                        .filter()
                          .xpath("//acme:record",ns)
                          .log(LoggingLevel.INFO,"Record : ${body}")
                          .to("mock:result")
                        .end();
            }
        };
    }

}
