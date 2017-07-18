package org.jboss.fuse.persistence.idempotent;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

       
import org.jboss.fuse.persistence.model.Incident;

import java.util.*;

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
