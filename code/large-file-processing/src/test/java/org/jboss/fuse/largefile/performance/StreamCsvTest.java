package org.jboss.fuse.largefile.performance;

import org.apache.camel.*;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.StopWatch;
import org.apache.camel.util.TimeUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamCsvTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CamelTestSupport.class);

    private int files = 10;
    private int rows = 500000;

    @Test
    public void testWithoutStream() throws Exception {
        StopWatch watch = new StopWatch();
        int total = files * rows;
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(total).create();
        context.startRoute("nostream");

        notify.matches(3, TimeUnit.MINUTES);
        log.info("Took " + watch.taken() + " millis to process " + total + " records without stream.");
    }

    @Test
    public void testWithStream() throws Exception {
        int total = files * rows;
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(total).create();

        // Start the route
        StopWatch watch = new StopWatch();
        context.startRoute("stream");

        notify.matches(3, TimeUnit.MINUTES);
        log.info("Took " + watch.taken() + " millis to process " + total + " records using stream.");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                    from("file://target/data?noop=true").id("nostream").noAutoStartup()
                       .split().tokenize("\n")
                       .log(LoggingLevel.DEBUG,"Record : ${body}");

                from("file://target/data?noop=true").id("stream").noAutoStartup()
                        .split().tokenize("\n").streaming()
                        .log(LoggingLevel.DEBUG,"Record : ${body}");
            }
        };

    }

}

