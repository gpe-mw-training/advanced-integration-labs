package org.jboss.fuse.persistence.jdbc;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.jdbc.JdbcConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.jboss.fuse.persistence.AbstractJdbcTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertAndSelectFromTest extends AbstractJdbcTestSupport {

    @EndpointInject(uri = "mock:insert")
    MockEndpoint mockInsert;

    @Test
    public void testInsertAndSelect() throws Exception {

        String INSERT_QUERY = "insert into REPORT.T_INCIDENT "
                + "(INCIDENT_REF,INCIDENT_DATE,GIVEN_NAME,FAMILY_NAME,SUMMARY,DETAILS,EMAIL,PHONE)"
                + " values "
                + "(:?ref, :?date, :?firstname, :?lastname, :?summary, :?details, :?email, :?phone)";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(JdbcConstants.JDBC_RETRIEVE_GENERATED_KEYS, true);
        params.put("ref","005");
        params.put("date","2016-03-25");
        params.put("firstname","Chad");
        params.put("lastname","Darby");
        params.put("summary","Incident Course-005");
        params.put("details","This is a report incident for course-005");
        params.put("email", "cdarby@redhat.com");
        params.put("phone", "+111 10 20 300");

        mockInsert.expectedMessageCount(1);

        template.sendBodyAndHeaders("direct://insert", INSERT_QUERY, params);
        mockInsert.assertIsSatisfied();

        List<Map<String, Object>> data = template.requestBody("direct://select","select * from REPORT.T_INCIDENT", List.class);
        Assert.assertEquals(5,data.size());

        Map<String, Object> row = data.get(4);
        assertEquals("5", row.get("INCIDENT_ID").toString());
        assertEquals("005", row.get("INCIDENT_REF"));
        assertEquals("Chad", row.get("GIVEN_NAME"));
        assertEquals("Darby", row.get("FAMILY_NAME"));
        assertEquals("cdarby@redhat.com", row.get("EMAIL"));
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/camelContext.xml");
    }
}