package org.jboss.fuse.persistence.jdbc;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.jdbc.JdbcConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertAndSelectFromTest  extends CamelSpringTestSupport {

    @EndpointInject(uri = "mock:insert")
    MockEndpoint mockInsert;

    @Test
    public void testInsertAndSelect() throws Exception {
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/sql/camelContext.xml");
    }
}