package org.jboss.fuse.largefile;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SimpleSplitTest extends CamelTestSupport {

    @Test
    public void testMessageToTokenize() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMessageCount(5);
        String message = "Hello\nWorld\nHow\nAre\nyou ?";

        template.sendBody("direct:start", message);

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                   .split(body(String.class).tokenize("\n")).streaming()
                   .to("mock:result");
            }
        };
    }

}
