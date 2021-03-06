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

        // now we send the exchange to the endpoint, and receives the response from Camel
        List<Map<String, Object>> data = template.requestBody("direct:hello", "SELECT * FROM REPORT.T_INCIDENT", List.class);

        // assertions of the response
        assertNotNull(data);
        assertEquals(4, data.size());
        Map<String, Object> row = data.get(0);
        assertEquals("1", row.get("INCIDENT_ID").toString());
        assertEquals("001", row.get("INCIDENT_REF"));
        assertEquals("Charles", row.get("GIVEN_NAME"));
        assertEquals("Moulliard", row.get("FAMILY_NAME"));
        assertEquals("cmoulliard@redhat.com", row.get("EMAIL"));
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/camelContext.xml");
    }
}