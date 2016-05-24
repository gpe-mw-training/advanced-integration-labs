package org.jboss.fuse.persistence;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

public class CamelJDBCH2Test extends AbstractJdbcTestSupport {


    @Test
    public void testJdbcRoutes() throws Exception {
        // first we create our exchange using the endpoint
        Endpoint endpoint = context.getEndpoint("direct:hello");
        Exchange exchange = endpoint.createExchange();
        // then we set the SQL on the in body
        exchange.getIn().setBody("select * from customer order by ID");

        // now we send the exchange to the endpoint, and receives the response from Camel
        Exchange result = template.send(endpoint, exchange);

        // assertions of the response
        assertNotNull(result);
        assertNotNull(result.getOut());
        List<Map<String, Object>> data = result.getOut().getBody(List.class);
        assertNotNull(data);
        assertEquals(3, data.size());
        Map<String, Object> row = data.get(0);
        assertEquals("cust1", row.get("ID"));
        assertEquals("jstrachan", row.get("NAME"));
        row = data.get(1);
        assertEquals("cust2", row.get("ID"));
        assertEquals("nsandhu", row.get("NAME"));
        // END SNIPPET: invoke
    }

    @Override protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            // lets add simple route
            public void configure() throws Exception {
                from("direct:hello").to("jdbc:testdb?readSize=100");
            }
            // END SNIPPET: route
        };
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/camelContext.xml");
    }
}