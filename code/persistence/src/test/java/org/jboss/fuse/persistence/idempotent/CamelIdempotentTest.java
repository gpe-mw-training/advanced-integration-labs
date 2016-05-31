package org.jboss.fuse.persistence.idempotent;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.direct.DirectConsumerNotAvailableException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.net.ntp.TimeStamp;
import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.*;

public class CamelIdempotentTest extends CamelSpringTestSupport {

    @EndpointInject(uri="direct:data-insert")
    ProducerTemplate template;

    @EndpointInject(uri="mock:result")
    MockEndpoint mockResult;

   @Test
    public void testSendDifferentCsvRecords() throws InterruptedException {
    }

    @Test
    public void testSendSomeCsvRecordWithSameHeader() throws InterruptedException {
    }

    @Test
    public void testStopStartCamelRoute() throws Exception {
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/idempotent/camelRoutes.xml");
    }
}
