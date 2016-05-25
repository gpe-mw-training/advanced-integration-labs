package org.jboss.fuse.persistence;

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

/**
 * Unit test based on user forum request about this component
 */
public abstract class AbstractJdbcTest extends CamelSpringTestSupport {
    
    protected EmbeddedDatabase db;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry reg = super.createRegistry();
        reg.bind("testdb", db);
        return reg;
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}