package org.jboss.fuse.persistence.jdbc;

import org.jboss.fuse.persistence.AbstractJdbcTest;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

public class SelectFromTest extends AbstractJdbcTest {

    @Test
    public void testSelectFrom() throws Exception {
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/sql/camelContext.xml");
    }
}