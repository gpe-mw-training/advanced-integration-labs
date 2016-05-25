package org.jboss.fuse.persistence.jdbc;

import org.jboss.fuse.persistence.AbstractJdbcTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectFromWithParamsTest extends AbstractJdbcTestSupport {

    @Test
    public void testNamedParametersQuery() throws Exception {

        Map<String, Object> jdbcParams = new HashMap<String, Object>();
        jdbcParams.put("firstname", "Charles");
        jdbcParams.put("lastname", "Moulliard");

        // now we send the exchange to the endpoint, and receives the response from Camel
        List<Map<String, Object>> data = template.requestBodyAndHeaders("direct://select", "SELECT * FROM REPORT.T_INCIDENT where GIVEN_NAME = :?firstname AND FAMILY_NAME = :?lastname", jdbcParams, List.class);

        // assertions of the response
        assertNotNull(data);
        assertEquals(2, data.size());

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