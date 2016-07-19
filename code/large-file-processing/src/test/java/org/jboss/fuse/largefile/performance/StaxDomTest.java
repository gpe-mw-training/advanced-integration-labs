package org.jboss.fuse.largefile.performance;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class StaxDomTest extends CamelTestSupport {

    @Override
    public void setUp() throws Exception {
        deleteDirectory("target/xtokenizer");
        super.setUp();
    }

    @Test
    public void testMessageToTokenizeWithXpath() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:xmltokenize");
        resultEndpoint.expectedMessageCount(4);
        String message = "<persons><person>James</person><person>Claus</person><person>Jonathan</person><person>Hadrian</person></persons>";

        template.sendBody("direct:start-xpath", message);

        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testMessageToTokenizeWithXTokenizer() throws Exception {
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
            XPathBuilder xPathBuilder = new XPathBuilder("//persons/person");

            public void configure() {
                from("file:target/xtokenizer")
                    .split().xtokenize("//orders/order",ns).streaming()
                    .to("mock:xtokenize");

                // Use DOM and load all XML structure in memory
                from("direct:start-xpath")
                    .split(xPathBuilder)
                    .to("mock:xmltokenize");
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
