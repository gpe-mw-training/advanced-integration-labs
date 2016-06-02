package org.jboss.fuse.transaction.route;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JTATxRouteTest extends CamelSpringTestSupport {

    @Override protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/transaction/camelContext.xml");
    }

    @Before
    public void cfgSystemProperties() {
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
    }

    @Test
    public void testRollbackRecord() throws Exception {
    }

}
