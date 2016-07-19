package org.jboss.fuse.largefile.performance;

import org.apache.camel.*;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.camel.util.TimeUtils;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SplitTest extends CamelTestSupport {

    private int size;
    private final AtomicInteger tiny = new AtomicInteger();
    private final AtomicInteger small = new AtomicInteger();
    private final AtomicInteger med = new AtomicInteger();
    private final AtomicInteger large = new AtomicInteger();
    private final StopWatch watch = new StopWatch();
    private Runtime runtime;
    private long usedMemoryBefore;

    @Produce
    private ProducerTemplate template;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void test20000() throws Exception {
        size = 20000;
        runBenchmark(size);
    }

    @Test
    public void test200000() throws Exception {
        size = 200000;
        runBenchmark(size);
    }

    private void runBenchmark(int Size) throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(size).create();

        template.sendBody("direct:data-" + size,createDataFile(log, size));

        boolean matches = notify.matches(60, TimeUnit.SECONDS);
        log.debug("Processed file with " + size + " elements in: " + TimeUtils.printDuration(watch.stop()));

        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        log.info("Memory increased : " + (usedMemoryAfter-usedMemoryBefore));

/*        log.debug("Processed " + tiny.get() + " tiny messages");
        log.debug("Processed " + small.get() + " small messages");
        log.debug("Processed " + med.get() + " medium messages");
        log.debug("Processed " + large.get() + " large messages");

        assertEquals((size / 10) * 4, tiny.get());
        assertEquals((size / 10) * 2, small.get());
        assertEquals((size / 10) * 3, med.get());
        assertEquals((size / 10) * 1, large.get());*/

        assertTrue("Should complete route", matches);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                String[] destinations = {"20000","200000"};
                for (String s : destinations) {
                    from("direct:data-" + s)
                            .process(new Processor() {
                                public void process(Exchange exchange) throws Exception {
                                    log.info("Starting to process file");
                                    watch.restart();
                                    runtime = Runtime.getRuntime();
                                    usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                                    log.info("Used Memory before : " + usedMemoryBefore);
                                }
                            })
                            .split().xpath("/orders/order")
                                .log(LoggingLevel.DEBUG,"Order splitted");
                }

            }
        };

    }

    public File createDataFile(Logger log, int size) throws Exception {
        deleteDirectory("target/data");
        createDirectory("target/data");

        log.info("Creating data file ...");

        File file = new File("target/data/data-" + Integer.toString(size) + ".xml");
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write("<orders>\n".getBytes());

        for (int i = 0; i < size; i++) {
            fos.write("<order>\n".getBytes());
            fos.write(("  <id>" + i + "</id>\n").getBytes());
            int num = i % 10;
            if (num >= 0 && num <= 3) {
                fos.write("  <amount>3</amount>\n".getBytes());
                fos.write("  <customerId>333</customerId>\n".getBytes());
            } else if (num >= 4 && num <= 5) {
                fos.write("  <amount>44</amount>\n".getBytes());
                fos.write("  <customerId>444</customerId>\n".getBytes());
            } else if (num >= 6 && num <= 8) {
                fos.write("  <amount>88</amount>\n".getBytes());
                fos.write("  <customerId>888</customerId>\n".getBytes());
            } else {
                fos.write("  <amount>123</amount>\n".getBytes());
                fos.write("  <customerId>123123</customerId>\n".getBytes());
            }
            fos.write("  <description>bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla</description>\n".getBytes());
            fos.write("</order>\n".getBytes());
        }

        fos.write("</orders>".getBytes());
        fos.close();

        log.info("Creating data file done.");

        return file;
    }

}

