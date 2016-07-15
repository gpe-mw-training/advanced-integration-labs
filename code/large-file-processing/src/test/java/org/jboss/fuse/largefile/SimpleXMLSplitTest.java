package org.jboss.fuse.largefile;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SimpleXMLSplitTest extends CamelTestSupport {

    @Test
    public void testMessageToTokenize() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMessageCount(4);
        String message = "<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>";

        template.sendBody("direct:start", message);

        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testGroupMessageToTokenize() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMessageCount(1);
        String message = "<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>";

        template.sendBody("direct:start-group", message);

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                   .split().tokenizeXML("person").streaming()
                   .to("mock:result");

                from("direct:start-group")
                        .split().tokenizeXML("person",4).streaming()
                        .to("mock:result");
            }
        };
    }

}
