package org.jboss.fuse.transaction.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class JTATxRouteTest extends CamelSpringTestSupport {

    @Override protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/transaction/camelContext.xml");
    }

    @Test
    public void testProducer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBody("direct:data-insert", "111,22-04-2016,Claus,Ibsen,incident camel-111,this is a report incident for camel-111,cibsen@gmail.com,+111 10 20 300");
        template.sendBody("direct:select",null);

        Exchange exchange = mock.getExchanges().get(0);
        assertMockEndpointsSatisfied();
        assertEquals("1, Claus, Ibsen, cibsen@gmail.com, +111 10 20 300, 111, this is a report incident for camel-111, incident camel-111",exchange.getIn().getBody());
        // assertEquals("Should have deleted record for id 1", new Integer(2), jdbcTemplate.queryForObject("select count(*) from projects", Integer.class));
    }

}
