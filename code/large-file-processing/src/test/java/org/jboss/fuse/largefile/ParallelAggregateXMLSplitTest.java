package org.jboss.fuse.largefile;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.List;

public class ParallelAggregateXMLSplitTest extends CamelTestSupport {

    @Override public void setUp() throws Exception {
        deleteDirectory("target/xtokenize");
        super.setUp();
    }

    @Test public void testParallel() throws Exception {
        String result = "a1,b2,c3,d4,e5,f6";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(result);

        template.sendBody("direct:start", "a,b,c,d,e,f");

        assertMockEndpointsSatisfied();
    }

    @Override protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {

            private int counter = 1;

            public void configure() {
                from("direct:start").split().tokenize(",").aggregationStrategy(new MyAggregationStrategy())
                        .process(new Processor() {
                            @Override public void process(Exchange exchange) throws Exception {
                                String body = (String) exchange.getIn().getBody();
                                exchange.getOut().setBody(body + Integer.toString(counter++));
                            }
                        }).log(">> Splitted msg : ${body}").end().to("mock:result");

            }
        };
    }

    private class MyAggregationStrategy implements AggregationStrategy {

        @Override public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                newExchange.getIn().setBody(new StringBuilder(newExchange.getIn().getBody(String.class)));
                return newExchange;
            }
            oldExchange.getIn().getBody(StringBuilder.class)
                    .append(",")
                    .append(newExchange.getIn().getBody(String.class));
            return oldExchange;
        }
    }
}
