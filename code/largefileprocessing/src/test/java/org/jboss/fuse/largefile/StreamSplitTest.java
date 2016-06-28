package org.jboss.fuse.largefile;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;

public class StreamSplitTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override public void configure() {
                from("").split().tokenize("").streaming().to("");
            }
        };
    }

}
