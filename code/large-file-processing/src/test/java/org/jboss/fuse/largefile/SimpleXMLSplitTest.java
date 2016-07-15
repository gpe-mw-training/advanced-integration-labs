package org.jboss.fuse.largefile;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SimpleXMLSplitTest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/xtokenizer");
        super.setUp();
    }

    @Test
    public void testMessageToTokenize() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:xmltokenize");
        resultEndpoint.expectedMessageCount(4);
        String message = "<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>";

        template.sendBody("direct:start", message);

        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testGroupMessageToTokenize() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:xmltokenize-group");
        resultEndpoint.expectedMessageCount(1);
        String message = "<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>";

        template.sendBody("direct:start-group", message);

        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testMessageWithXTokenize() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:xtokenize");
        mock.expectedMessageCount(3);
        mock.message(0).body().isEqualTo("<order id=\"1\" xmlns=\"http:acme.com\">Camel in Action</order>");
        mock.message(1).body().isEqualTo("<order id=\"2\" xmlns=\"http:acme.com\">ActiveMQ in Action</order>");
        mock.message(2).body().isEqualTo("<order id=\"3\" xmlns=\"http:acme.com\">DSL in Action</order>");

        String body = createBody();
        template.sendBodyAndHeader("file:target/xtokenizer", body, Exchange.FILE_NAME, "orders.xml");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {

        return new RouteBuilder() {

            Namespaces ns = new Namespaces("", "http:acme.com");

            public void configure() {
                from("direct:start")
                   .split().tokenizeXML("person").streaming()
                   .to("mock:xmltokenize");

                from("direct:start-group")
                    .split().tokenizeXML("person",4).streaming()
                    .to("mock:xmltokenize-group");

                from("file:target/xtokenizer")
                    .split().xtokenize("//orders/order",ns).streaming()
                    .to("mock:xtokenize");
            }
        };
    }

    protected String createBody() {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>\n");
        sb.append("<orders xmlns=\"http:acme.com\">\n");
        sb.append("  <order id=\"1\">Camel in Action</order>\n");
        sb.append("  <order id=\"2\">ActiveMQ in Action</order>\n");
        sb.append("  <order id=\"3\">DSL in Action</order>\n");
        sb.append("  <order id=\"4\" xmlns=\"\">Illegal Action</order>\n");
        sb.append("</orders>");
        return sb.toString();
    }

}
