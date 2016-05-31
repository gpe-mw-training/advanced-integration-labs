package org.jboss.fuse.persistence.jpa;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.jboss.fuse.persistence.model.SendEmail;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;

public class JpaConsumerTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "mock:result")
    MockEndpoint mock;

    protected static final String SELECT_ALL_STRING = "select x from " + SendEmail.class.getName() + " x";

    @Test
    public void testInsertAndReceive() throws Exception {
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/jpa/springJpaRouteTest.xml");
    }
}