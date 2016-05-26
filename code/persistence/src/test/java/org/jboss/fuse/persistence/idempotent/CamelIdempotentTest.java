package org.jboss.fuse.persistence.idempotent;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
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
        mockResult.expectedMessageCount(2);
        template.requestBodyAndHeader("111,22-04-2016,Claus,Ibsen,incident camel-111,this is a report incident for camel-111,cibsen@gmail.com,+111 10 20 300","CamelRecord",1);
        template.requestBodyAndHeader("222,18-05-2016,Claus,Ibsen,incident camel-222,this is a report incident for camel-222,cibsen@gmail.com,+111 10 20 300","CamelRecord",2);
        mockResult.assertIsSatisfied();
    }

    @Test
    public void testSendSomeCsvRecordWithSameHeader() throws InterruptedException {
        mockResult.expectedMessageCount(2);
        template.requestBodyAndHeader("111,22-04-2016,Claus,Ibsen,incident camel-111,this is a report incident for camel-111,cibsen@gmail.com,+111 10 20 300","CamelRecord",1);
        template.requestBodyAndHeader("222,18-05-2016,Claus,Ibsen,incident camel-222,this is a report incident for camel-222,cibsen@gmail.com,+111 10 20 300","CamelRecord",2);
        template.requestBodyAndHeader("333,18-05-2016,Claus,Ibsen,incident camel-333,this is a report incident for camel-333,cibsen@gmail.com,+111 10 20 300","CamelRecord",1);
        mockResult.assertIsSatisfied();
    }

    @Test
    public void testStopStartCamelRoute() throws Exception {
        mockResult.expectedMessageCount(1);

        template.requestBodyAndHeader("111,22-04-2016,Claus,Ibsen,incident camel-111,this is a report incident for camel-111,cibsen@gmail.com,+111 10 20 300","CamelRecord",1);
        stopCamelContext();

        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:mem:idempotentReport");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from CAMEL_MESSAGEPROCESSED");
            while (rs.next()) {
                Assert.assertEquals("1",rs.getString("messageId"));
                Assert.assertEquals("DirectConsumer",rs.getString("processorName"));
            }
        } catch(Exception e) {
            System.out.print("Something happened");
        } finally {
            conn.close();
        }

        startCamelContext();
        template.start();
        template.requestBodyAndHeader("333,18-05-2016,Claus,Ibsen,incident camel-333,this is a report incident for camel-333,cibsen@gmail.com,+111 10 20 300","CamelRecord",1);

        mockResult.assertIsSatisfied();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/jboss/fuse/persistence/idempotent/camelRoutes.xml");
    }
}
