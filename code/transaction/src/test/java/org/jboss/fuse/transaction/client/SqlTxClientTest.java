package org.jboss.fuse.transaction.client;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.Map;

public class SqlTxClientTest extends CamelTestSupport {

    private EmbeddedDatabase db;
    private JdbcTemplate jdbcTemplate;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry reg = super.createRegistry();

        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.DERBY).addScript("sql/createAndPopulateDatabase.sql").build();
        reg.bind("testdb", db);

        jdbcTemplate = new JdbcTemplate(db);

        DataSourceTransactionManager dtm = new DataSourceTransactionManager(db);
        dtm.setDataSource(db);
        reg.bind("txManager", dtm);

        SqlComponent sql = new SqlComponent();
        sql.setDataSource(db);
        reg.bind("sql",sql);

        return reg;
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        db.shutdown();
    }

    @Test
    public void testConsume() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);

        assertMockEndpointsSatisfied();

        assertEquals("Should Not have deleted all rows", new Integer(3), jdbcTemplate.queryForObject("select count(*) from projects", Integer.class));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("sql:select * from projects order by id?consumer.onConsume=delete from projects where id = :#id")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                throw new Exception("forced Exception");
                            }
                        })
                        .to("mock:result");
            }
        };
    }
}
