package org.jboss.fuse.transaction.client;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.jboss.fuse.transaction.model.Project;
import org.junit.Test;

public class JpaTxRollbackTest extends AbstractJpaTest {

    @Override
    protected CamelContext createCamelContext() throws Exception {
        applicationContext = new org.springframework.context.support.ClassPathXmlApplicationContext(
                "org/jboss/fuse/transaction/springJpaRouteTest.xml");
        return SpringCamelContext.springCamelContext(applicationContext);
    }

    @Test
    public void testRollBack() throws Exception {
    }
}
