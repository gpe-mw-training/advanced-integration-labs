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

public class SqlTxRollbackConsumerTest extends CamelTestSupport {

    private EmbeddedDatabase db;
    private JdbcTemplate jdbcTemplate;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry reg = super.createRegistry();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        db.shutdown();
    }

    @Test
    public void testConsume() throws Exception {
    }
}
